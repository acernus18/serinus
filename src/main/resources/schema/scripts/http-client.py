
import http.client

conn = http.client.HTTPConnection("127,0,0,1")

headers = {
    'User-Agent': "PostmanRuntime/7.15.0",
    'Accept': "*/*",
    'Cache-Control': "no-cache",
    'Postman-Token': "c24e83a7-23c9-495d-a86a-47ddb0ccec76,8aeee615-996b-4837-a3c1-59b23b11a1ec",
    'Host': "127.0.0.1:8080",
    'cookie': "JSESSIONID=2448b051-989a-4894-bdc3-383d71a3988c",
    'accept-encoding': "gzip, deflate",
    'Connection': "keep-alive",
    'cache-control': "no-cache"
}

conn.request("GET", "system,user,list", headers=headers)

res = conn.getresponse()
data = res.read()

print(data.decode("utf-8"))

