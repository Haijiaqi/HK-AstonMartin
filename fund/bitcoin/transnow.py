import demjson
import os
import requests
import time
def pause(h, m):
    while True:
        permittime = time.gmtime(time.time() + 28800)
        if permittime[3] == h and permittime[4] == m:
            break
        time.sleep(58)
def getparam(index):
    basepath = os.getcwd() + '/' + "D7"
    fp = open(basepath + ".txt")#D:\\Aproject\\params.txt", 'r')
    result = ''
    for aline in fp:
        result += aline
    result = demjson.decode(result)
    fi = open(basepath + "points.txt", 'w')
    #result = demjson.decode('{' + result + '}')[index]
    stamp = 1665412800
    for key in result["data"]["body"]["data"]["points"].items():
        #print(key[0], key[1], sep=":")
        aline = key[0] + "," + str(key[1]["v"][0])
        print(aline)
        '''amp + 900
        if stamp > 1666017550:  https://www.okx.com/api/v5/market/history-index-candles?instId=BTC-USD&bar=15m&before=1655481600000&after=1655568000000
        break'''
        fi.write(aline + '\n')
    fi.close()
    #print(result["data"]["body"]["data"]["points"]["1665414600"]["v"][4])
    return result
def getparamfromnet(rawdata, path):
    result = demjson.decode(rawdata)
    fi = open(path, 'a')
    #result = demjson.decode('{' + result + '}')[index]
    stamp = 1665412800
    arrays = []
    for key in result["data"]:
        #print(key[0], key[1], sep=":")
        aline = key[0] + "," + str((eval(key[1]) + eval(key[2]) + eval(key[3]) + eval(key[4])) / 4)
        '''amp + 900
        if stamp > 1666017550:  https://www.okx.com/api/v5/market/history-index-candles?instId=BTC-USD&bar=15m&before=1655481600000&after=1655568000000
        break'''
        arrays.append(aline)
    arrays.reverse()
    i = len(arrays)
    for aline in arrays:
        fi.write(aline + '\n')
    fi.close()
    #print(result["data"]["body"]["data"]["points"]["1665414600"]["v"][4])
    return result
def saveparamfromnet(rawdata, path):
    result = demjson.decode(rawdata)
    fo = open(path, 'r')
    file = []
    for aline in fo:
        file.append(aline)
    fo.close()
    length = len(file)
    start = length - 120 + 1
    if start < 0:
        start = 0
    fi = open(path, 'w')
    fi.write('')
    fi.close()
    fi = open(path, 'a')
    for i in range(start, length):
        fi.write(file[i])
    aline = result["data"][0]["ts"] + ',' + result["data"][0]["idxPx"]
    fi.write(aline + '\n')
    fi.close()
    return result
    
def getHTMLText(url):
    try:
        #kv = {'Referer':'http://fund.eastmoney.com/data/fundranking.html'}
        r = requests.get(url)#, headers = kv)
        r.raise_for_status()
        r.encoding = r.apparent_encoding
        return r.text
    except:
        return ""
        
def turnpage(url, start, inter, page):
    pre = "before=" + str(start) + '&'
    pi = "before=" + str(start + inter * (page - 1) - 1) + '&'
    url = url.replace(pre, pi)
    pre = "after=" + str(start + inter)
    pi = "after=" + str(start + inter + inter * (page - 1) - 1)
    print(pi)
    return url.replace(pre, pi)

name = 'okex15s.txt'
#inter = 86400000
#start = 1655481600000
basepath = os.getcwd() + '/'#fund/coin/'
while True:
    url = 'https://www.okx.com/api/v5/market/index-tickers?instId=BTC-USD'
    text = getHTMLText(url)
    saveparamfromnet(text, basepath + name)
    time.sleep(15)
        
fi = open(basepath + name, 'w')
fi.write('')
fi.close()
for i in range(1000):
    if (i * inter + start) < time.time() * 1000:
        url = turnpage('https://www.okx.com/api/v5/market/history-index-candles?instId=BTC-USD&bar=15m&before=1655481600000&after=1655568000000', start, inter, i + 1)
        text = getHTMLText(url)
        time.sleep(0.5)
        getparamfromnet(text, basepath + name)

#text = getparam(0)
