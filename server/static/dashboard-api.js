/* 智慧实验室电子班牌 · 管理后台数据接入层
 * 在既有 templates/index.html 之上补充：总览统计、最近动态、设备中心、考勤、
 * 安全管理、环境监控、AI 培养方案的真实数据渲染。
 * 仅调用 /api/v1 只读接口，不改动原有业务逻辑。
 */
(function () {
  'use strict';

  var deviceCache = { list: [], categories: [] };
  var labIdCache = null;

  async function resolveLabId() {
    if (labIdCache) return labIdCache;
    try {
      var res = await apiRequest('/labs');
      var list = res.data || [];
      if (Array.isArray(list) && list.length) labIdCache = list[0].id;
    } catch (e) {
      console.warn('实验室列表加载失败', e);
    }
    return labIdCache;
  }

  function el(id) { return document.getElementById(id); }

  function setText(id, value) {
    var node = el(id);
    if (node) node.textContent = value;
  }

  function relTime(iso) {
    if (!iso) return '';
    var d = new Date(iso);
    if (isNaN(d.getTime())) return String(iso).slice(0, 16).replace('T', ' ');
    var diff = (Date.now() - d.getTime()) / 1000;
    if (diff < 60) return '刚刚';
    if (diff < 3600) return Math.floor(diff / 60) + ' 分钟前';
    if (diff < 86400) return Math.floor(diff / 3600) + ' 小时前';
    return Math.floor(diff / 86400) + ' 天前';
  }

  function esc(text) {
    if (text === null || text === undefined) return '';
    return String(text).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function today() {
    var d = new Date();
    return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
  }

  function drawLine(canvasId, points) {
    var canvas = el(canvasId);
    if (!canvas) return;
    var rect = canvas.getBoundingClientRect();
    if (!rect.width || !rect.height) return;
    var dpr = window.devicePixelRatio || 1;
    canvas.width = Math.round(rect.width * dpr);
    canvas.height = Math.round(rect.height * dpr);
    var ctx = canvas.getContext('2d');
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    var w = rect.width, h = rect.height;
    var padL = 40, padR = 10, padT = 12, padB = 22;
    ctx.clearRect(0, 0, w, h);
    ctx.fillStyle = '#94a3b8';
    ctx.font = '11px -apple-system, PingFang SC, sans-serif';
    if (!points || points.length === 0) {
      ctx.fillText('暂无数据', padL, h / 2);
      return;
    }
    var values = points.map(function (p) { return p[1]; });
    var min = Math.min.apply(null, values);
    var max = Math.max.apply(null, values);
    if (min === max) { min -= 1; max += 1; }
    var pad = (max - min) * 0.18;
    min -= pad; max += pad;
    var plotW = w - padL - padR, plotH = h - padT - padB;
    ctx.strokeStyle = '#eef1f7';
    ctx.lineWidth = 1;
    for (var i = 0; i <= 4; i++) {
      var gy = padT + plotH * i / 4;
      ctx.beginPath();
      ctx.moveTo(padL, gy);
      ctx.lineTo(w - padR, gy);
      ctx.stroke();
      ctx.fillText((max - (max - min) * i / 4).toFixed(1), 4, gy + 4);
    }
    var px = function (idx) {
      return points.length === 1 ? padL + plotW / 2 : padL + plotW * idx / (points.length - 1);
    };
    var py = function (v) { return padT + plotH * (1 - (v - min) / (max - min)); };
    ctx.beginPath();
    points.forEach(function (p, idx) {
      var x = px(idx), y = py(p[1]);
      if (idx === 0) ctx.moveTo(x, y); else ctx.lineTo(x, y);
    });
    ctx.strokeStyle = '#3b82f6';
    ctx.lineWidth = 2;
    ctx.stroke();
    ctx.lineTo(px(points.length - 1), padT + plotH);
    ctx.lineTo(px(0), padT + plotH);
    ctx.closePath();
    var grad = ctx.createLinearGradient(0, padT, 0, padT + plotH);
    grad.addColorStop(0, 'rgba(59,130,246,0.28)');
    grad.addColorStop(1, 'rgba(59,130,246,0.02)');
    ctx.fillStyle = grad;
    ctx.fill();
    ctx.fillStyle = '#94a3b8';
    var step = Math.max(1, Math.floor(points.length / 5));
    for (var k = 0; k < points.length; k += step) {
      ctx.fillText(points[k][0], px(k) - 12, h - 6);
    }
  }

  /* ---------------- 总览 ---------------- */
  async function loadDashboard() {
    try {
      var results = await Promise.all([
        apiRequest('/devices?page=1&limit=1'),
        apiRequest('/projects?page=1&limit=1'),
        apiRequest('/maintenance-orders/statistics?period=month'),
        apiRequest('/attendance?page=1&limit=1&date=' + today()),
        apiRequest('/announcements?page=1&limit=4')
      ]);
      setText('stat-devices', ((results[0].data || {}).pagination || {}).total || 0);
      setText('stat-projects', ((results[1].data || {}).pagination || {}).total || 0);
      var ms = results[2].data || {};
      setText('stat-alerts', (ms.pending_orders || 0) + (ms.in_progress_orders || 0));
      setText('stat-attendance', ((results[3].data || {}).pagination || {}).total || 0);
      renderActivity(results[4].data || {});
    } catch (e) {
      console.warn('总览统计加载失败', e);
    }
    loadDashboardChart();
  }

  function renderActivity(payload) {
    var box = el('recent-activity');
    if (!box) return;
    var items = (payload.items || []).map(function (a) {
      return {
        color: a.is_important ? 'bg-red-500' : 'bg-blue-500',
        text: (a.author_name ? a.author_name + ' 发布：' : '') + a.title,
        time: relTime(a.created_at)
      };
    });
    if (!items.length) {
      box.innerHTML = '<p class="text-sm text-gray-500">暂无动态</p>';
      return;
    }
    box.innerHTML = items.map(function (item) {
      return '<div class="flex items-start space-x-3">' +
        '<div class="w-2 h-2 ' + item.color + ' rounded-full mt-2"></div>' +
        '<div><p class="text-sm text-gray-800">' + esc(item.text) + '</p>' +
        '<p class="text-xs text-gray-500">' + esc(item.time) + '</p></div></div>';
    }).join('');
  }

  async function loadDashboardChart() {
    try {
      var labId = await resolveLabId();
      var url = '/environmental/data?page=1&limit=200&sensor_type=' + encodeURIComponent('温度');
      if (labId) url += '&lab_id=' + labId;
      var res = await apiRequest(url);
      var items = (res.data || {}).items || [];
      items.sort(function (a, b) { return a.timestamp < b.timestamp ? -1 : 1; });
      drawLine('dash-temp-chart', items.slice(-24).map(function (i) {
        return [String(i.timestamp).slice(11, 16), Number(i.value)];
      }));
    } catch (e) {
      drawLine('dash-temp-chart', []);
    }
  }

  /* ---------------- 设备中心 ---------------- */
  async function loadDevices() {
    try {
      var res = await Promise.all([
        apiRequest('/devices?page=1&limit=100'),
        apiRequest('/device-categories')
      ]);
      deviceCache.list = ((res[0].data || {}).items) || [];
      deviceCache.categories = res[1].data || [];
      fillDeviceFilters();
      renderDevices();
    } catch (e) {
      console.warn('设备加载失败', e);
    }
  }

  function fillDeviceFilters() {
    var sel = el('device-filter-category');
    if (!sel) return;
    var current = sel.value;
    sel.innerHTML = '<option value="">全部分类</option>' + deviceCache.categories.map(function (c) {
      return '<option value="' + c.id + '">' + esc(c.category_name) + '</option>';
    }).join('');
    sel.value = current || '';
  }

  function deviceBadge(status) {
    var map = {
      '可用': 'bg-green-100 text-green-800',
      '使用中': 'bg-yellow-100 text-yellow-800',
      '维修中': 'bg-red-100 text-red-800',
      '报废': 'bg-gray-200 text-gray-600'
    };
    return map[status] || 'bg-gray-100 text-gray-600';
  }

  function renderDevices() {
    var grid = el('devices-grid');
    if (!grid) return;
    var catId = (el('device-filter-category') || {}).value || '';
    var status = (el('device-filter-status') || {}).value || '';
    var keyword = ((el('device-filter-search') || {}).value || '').trim();
    var list = deviceCache.list.filter(function (d) {
      if (catId && String(d.category_id) !== String(catId)) return false;
      if (status && d.status !== status) return false;
      if (keyword && (d.device_name + ' ' + (d.model || '')).indexOf(keyword) === -1) return false;
      return true;
    });
    if (!list.length) {
      grid.innerHTML = '<div class="col-span-full bg-white rounded-xl shadow-sm p-8 text-center text-gray-500">没有符合条件的设备</div>';
      return;
    }
    grid.innerHTML = list.map(function (d) {
      return '<div class="bg-white rounded-xl shadow-sm overflow-hidden card-hover transition-all duration-300">' +
        '<div class="h-32 bg-gray-100 flex items-center justify-center"><i data-lucide="cpu" class="w-12 h-12 text-gray-400"></i></div>' +
        '<div class="p-4">' +
        '<h4 class="font-semibold text-gray-800">' + esc(d.device_name) + '</h4>' +
        '<p class="text-sm text-gray-600 truncate" title="' + esc(d.model) + '">' + esc(d.model || '-') + '</p>' +
        '<div class="flex items-center justify-between mt-2">' +
        '<span class="px-2 py-1 ' + deviceBadge(d.status) + ' text-xs rounded-full">' + esc(d.status) + '</span>' +
        '<span class="text-xs text-gray-500">' + esc(d.location || '') + '</span>' +
        '</div></div></div>';
    }).join('');
    if (window.lucide) lucide.createIcons();
  }

  /* ---------------- 考勤记录 ---------------- */
  async function loadAttendance() {
    var box = el('attendance-list');
    if (!box) return;
    try {
      var res = await apiRequest('/attendance?page=1&limit=30');
      var items = ((res.data || {}).items) || [];
      if (!items.length) {
        box.innerHTML = '<p class="text-center text-gray-500 py-8">暂无考勤记录</p>';
        return;
      }
      var rows = items.map(function (r) {
        return '<tr class="hover:bg-gray-50">' +
          '<td class="px-6 py-3 text-sm text-gray-900">' + esc(r.full_name || r.user_id) + '</td>' +
          '<td class="px-6 py-3 text-sm text-gray-600">' + esc(String(r.check_in_time || '-').replace('T', ' ').slice(0, 16)) + '</td>' +
          '<td class="px-6 py-3 text-sm text-gray-600">' + esc(String(r.check_out_time || '-').replace('T', ' ').slice(0, 16)) + '</td>' +
          '<td class="px-6 py-3 text-sm text-gray-600">' + esc(r.method || '-') + '</td>' +
          '<td class="px-6 py-3 text-sm text-gray-600">' + esc(r.emotion_status || '-') + '</td>' +
          '</tr>';
      }).join('');
      box.innerHTML = '<table class="min-w-full divide-y divide-gray-200">' +
        '<thead class="bg-gray-50"><tr>' +
        '<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">姓名</th>' +
        '<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">签到时间</th>' +
        '<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">签退时间</th>' +
        '<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">方式</th>' +
        '<th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">AI 情绪</th>' +
        '</tr></thead><tbody class="bg-white divide-y divide-gray-200">' + rows + '</tbody></table>';
    } catch (e) {
      box.innerHTML = '<p class="text-center text-red-500 py-8">考勤数据加载失败</p>';
    }
  }

  /* ---------------- 安全管理 ---------------- */
  async function loadSafety() {
    var box = el('safety-list');
    if (!box) return;
    try {
      var res = await apiRequest('/safety-guidelines?page=1&limit=20');
      var items = ((res.data || {}).items) || [];
      if (!items.length) {
        box.innerHTML = '<p class="text-center text-gray-500 py-8">暂无安全须知</p>';
        return;
      }
      box.innerHTML = items.map(function (g) {
        return '<div class="bg-white rounded-xl shadow-sm p-6">' +
          '<div class="flex items-center justify-between mb-2">' +
          '<h4 class="text-lg font-semibold text-gray-800">' + esc(g.title) + '</h4>' +
          '<div class="flex items-center gap-2">' +
          '<span class="px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded-full">' + esc(g.category) + '</span>' +
          '<span class="text-xs text-gray-500">' + esc(g.version || '') + '</span>' +
          '</div></div>' +
          '<p class="text-sm text-gray-600 whitespace-pre-line leading-relaxed">' + esc(g.content) + '</p></div>';
      }).join('');
    } catch (e) {
      box.innerHTML = '<p class="text-center text-red-500 py-8">安全须知加载失败</p>';
    }
  }

  /* ---------------- AI 培养方案 ---------------- */
  async function loadAiPlans() {
    var box = el('ai-plans-list');
    if (!box) return;
    try {
      var res = await apiRequest('/ai-training-plans?page=1&limit=20');
      var items = ((res.data || {}).items) || [];
      if (!items.length) {
        box.innerHTML = '<p class="text-center text-gray-500 py-8">暂无培养方案</p>';
        return;
      }
      box.innerHTML = items.map(function (p) {
        var plan = p.plan_content || {};
        var tasks = (plan.weekly_tasks || []).slice(0, 3).map(function (t) {
          return '<li class="text-sm text-gray-600">第 ' + esc(t.week) + ' 周：' + esc(t.task) + '</li>';
        }).join('');
        return '<div class="bg-white rounded-xl shadow-sm p-6">' +
          '<div class="flex items-center justify-between mb-2">' +
          '<h4 class="text-lg font-semibold text-gray-800">' + esc(p.user_name || ('用户 ' + p.user_id)) + '</h4>' +
          '<span class="px-2 py-1 ' + (p.status === '已完成' ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700') + ' text-xs rounded-full">' + esc(p.status || '-') + '</span>' +
          '</div>' +
          '<p class="text-sm text-gray-700 mb-2">' + esc(plan.goal || '') + '</p>' +
          '<p class="text-xs text-gray-500 mb-1">周期：' + esc(plan.duration_weeks || '-') + ' 周 · 导师：' + esc(plan.mentor || '-') + '</p>' +
          '<ul class="list-disc list-inside space-y-1">' + tasks + '</ul></div>';
      }).join('');
    } catch (e) {
      box.innerHTML = '<p class="text-center text-red-500 py-8">培养方案加载失败</p>';
    }
  }

  /* ---------------- 环境监控 ---------------- */
  async function loadEnvironment() {
    try {
      var labId = await resolveLabId();
      var labQuery = labId ? '?lab_id=' + labId : '';
      var dataQuery = '/environmental/data?page=1&limit=200&sensor_type=' + encodeURIComponent('温度') + (labId ? '&lab_id=' + labId : '');
      var res = await Promise.all([
        apiRequest('/environmental/data/latest' + labQuery),
        apiRequest(dataQuery)
      ]);
      var latest = res[0].data || {};
      if (latest['温度']) setText('env-temp', Number(latest['温度'].value).toFixed(1) + '°C');
      if (latest['湿度']) setText('env-hum', Number(latest['湿度'].value).toFixed(1) + '%');
      if (latest['CO2']) setText('env-co2', Number(latest['CO2'].value).toFixed(0) + 'ppm');
      var items = ((res[1].data || {}).items) || [];
      items.sort(function (a, b) { return a.timestamp < b.timestamp ? -1 : 1; });
      drawLine('env-chart', items.slice(-24).map(function (i) {
        return [String(i.timestamp).slice(11, 16), Number(i.value)];
      }));
    } catch (e) {
      console.warn('环境数据加载失败', e);
    }
  }

  /* ---------------- 页面切换钩子 ---------------- */
  var loaders = {
    dashboard: loadDashboard,
    devices: loadDevices,
    attendance: loadAttendance,
    safety: loadSafety,
    'ai-plans': loadAiPlans,
    environment: loadEnvironment
  };

  function ensureLoaded(name) {
    var fn = loaders[name];
    if (!fn) return;
    fn();
  }

  function hook() {
    if (typeof window.showPage === 'function' && !window.__ssPageHooked) {
      var orig = window.showPage;
      window.showPage = function (name) {
        orig(name);
        try { ensureLoaded(name); } catch (e) { console.warn(e); }
      };
      window.__ssPageHooked = true;
    }
    var catSel = el('device-filter-category');
    var statusSel = el('device-filter-status');
    var searchInput = el('device-filter-search');
    if (catSel) catSel.addEventListener('change', renderDevices);
    if (statusSel) statusSel.addEventListener('change', renderDevices);
    if (searchInput) searchInput.addEventListener('input', renderDevices);
    ensureLoaded('dashboard');
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () { setTimeout(hook, 400); });
  } else {
    setTimeout(hook, 400);
  }

  var resizeTimer = null;
  window.addEventListener('resize', function () {
    clearTimeout(resizeTimer);
    resizeTimer = setTimeout(function () {
      var dash = el('dashboard-page'), env = el('environment-page');
      if (dash && !dash.classList.contains('hidden')) loadDashboardChart();
      if (env && !env.classList.contains('hidden')) loadEnvironment();
    }, 300);
  });
})();
