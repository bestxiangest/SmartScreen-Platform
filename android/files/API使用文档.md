## 天气类(高德地图天气API)
API_KEY=06ab3ec992282fb5e6d3c5bb9604af7f
南昌市新建区城市编码adcode=360112

### 天气
请求南昌市新建区天气
``` 
https://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=	06ab3ec992282fb5e6d3c5bb9604af7f
```
返回值：
```
{
    "status": "1",
    "count": "1",
    "info": "OK",
    "infocode": "10000",
    "lives": [
        "0": {
            "province": "江西",
            "city": "新建区",
            "adcode": "360112",
            "weather": "阴",
            "temperature": "29",
            "winddirection": "南",
            "windpower": "≤3",
            "humidity": "62",
            "reporttime": "2025-07-07 17:03:04",
            "temperature_float": "29.0",
            "humidity_float": "62.0"
        }
    ]
}
```
