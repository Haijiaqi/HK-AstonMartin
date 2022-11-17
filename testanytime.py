import requests
import random
import time
import demjson
import os
import shutil

def getrealtoday():
    t = time.gmtime(time.time() + 28800)
    today = time.strftime('%Y-%m-%d', t)
    return today

def getHTMLText(url):
    try:
        kv = {'Referer':'http://fund.eastmoney.com/data/fundranking.html'}
        r = requests.get(url, headers = kv)
        r.raise_for_status()
        r.encoding = r.apparent_encoding
        return r.text
    except:
        return ""
def totimestamp(timestring, split):
    try:
        m = ['%Y', '%m', '%d']
        model = split.join(m)
        t = time.strptime(timestring, model)
        return str(time.mktime(t))
    except:
        return '0'
def main():
    backlist = ['invest.txt']
    basepath = os.getcwd() + '/' + "work/coin/paint/invest.txt"#fund/coin/'
    basepath1 = os.getcwd() + '/' + "work/coin/paint/invest" + getrealtoday + ".txt"#fund/coin/'
    shutil.copy(basepath, basepath1)# param = loadobj(basepath + "work/coin/paint/processInfo.txt")
    
    basepath = 'C:/b/'
    url = 'http://fund.eastmoney.com/data/FundPicData.aspx?bzdm=000063&n=0&dt=year&vname=ljsylSVG_PicData&r=0.9690231511789829'
    start = time.perf_counter()
    html = getHTMLText(url)
    html = html[html.index('"') + 1: html.index(';')]
    afund = html.split('|')   
    testpath = basepath + 'test/000063.txt'
    try:
        fi = open(testpath, 'w')
        for item in afund:
            apoint = item.split('_')[0:2]
            if apoint[1] != '':
                apoint[0] = totimestamp(apoint[0],'/')
                fi.write(','.join(apoint) + '\n')
        end = time.perf_counter()
        interval = end - start
        print("预测试使用" + str(interval) + "秒。")
        fi.close()
    except:
        interval = 0.5
        print("预测试失败，使用定值" + str(interval) + "秒。")

main()
