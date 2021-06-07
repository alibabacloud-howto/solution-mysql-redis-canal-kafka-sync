#!/usr/bin/python3

import cgitb
import redis
import json

r = redis.StrictRedis(host='r-gs58f59dc5666be4.redis.singapore.rds.aliyuncs.com', port=6379, db=0, password='Aliyun-test')

cgitb.enable()
print("Content-Type: text/html;charset=utf-8")
print()

keys = r.keys()

print ("<h1>Redis</h1>")
print ("<table border=1><tr><td><b>id</b></td><td><b>username</b></td><td><b>password</b></td><td><b>iphone</b></td><td><b>addr</b></td></tr>")

for key in keys:
    print("<tr>")
    dic1 = json.loads(r.get(key).decode("utf-8"))
    print ("<td>", dic1.get("id", "null"), '</td>')
    print ("<td>", dic1.get("username", "null"), '</td>')
    print ("<td>", dic1.get("password", "null"), '</td>')
    print ("<td>", dic1.get("phone","null"), '</td>')
    print ("<td>", dic1.get("addr", "null"), '</td>')
    print("<tr/>")
print ("</table><br/><br/>")
