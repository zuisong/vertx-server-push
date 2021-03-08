import requests

def req():
    resp = requests.get("http://localhost:8080/time", headers={
        "hotelCode": "123",
        'username': "test"
    })

    print(resp.content)
import _thread as thread

for i in range(1000):
    thread.start_new_thread(req,())
req()