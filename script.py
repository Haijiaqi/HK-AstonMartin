import matplotlib.pyplot as plt 
import numpy as np
import os
import demjson
import time
def loadparam(path):
    fp = open(path, 'r')
    for aline in fp:
        params = aline.split(';')
        path = params[0].split('\\')
        path = '\\\\'.join(path)
        '''maxi = eval(params[1])
        risklevel = eval(params[2])
        recentnum = eval(params[3])'''
        tab = params[1]
    fp.close()
    return path, tab
def loadobj(path):
    fp = open(path, 'r')
    params = ''
    for aline in fp:
        params += aline
    fp.close()
    return demjson.decode(params)
def writeline(path, line):
    fp = open(path, 'w')
    fp.write(line)
    fp.close()

def getXY(path, outsize):
    x = []
    y = []
    fo = open(path, 'r')
    i = 0
    for aline in fo:
        try:
            code = aline.split(',')
            x.append(eval(code[0]))
            y.append(eval(code[1]))
            i += 1
            if i == outsize:
                break
        except:
            print("paint error <<" + path)
    fo.close()
    return x, y

def getXYp(path, outsize, divide):
    x = []
    y = []
    fo = open(path, 'r')
    i = 0
    for aline in fo:
        if i == 0:
            startX = eval(aline.split(',')[0])
            startY = eval(aline.split(',')[1])
        try:
            code = aline.split(',')#1595779200#1593360000#
            x.append((eval(code[0]) - startX) / divide)
            y.append(eval(code[1]) - startY)
            i += 1
            if i == outsize:
                break
        except:
            print("paint error <<" + path)
    fo.close()
    return x, y

'''[{"struct":"@15500%2@16750%1@19000%0","amount":40.019260651332765,"NAV":17428.4,"cost":-14.615060245843722,
"flag":40,"start":40,"paint":0,"type":"BTC-USDT","fold":false,"extrapolation":3,"balance":15.397804542788506,
"name":"BTC","inrates":0.0008,"cash":0.7704260533105529,"order":2},
{"struct":"@1700%2@1750%1@1900%0","amount":40.01039271746935,"NAV":1835,"cost":0.4148819719262,
"flag":40,"start":40,"paint":0,"type":"YFII-USDT","fold":false,"extrapolation":3,"balance":0.000038438463236349785,
"name":"YFII","inrates":0.0008,"cash":0.4149203796386658,"order":2}]'''
def main():
    time.sleep(1)
    basepath = os.getcwd() + '/work/coin/paint/'
    path = basepath + "list.txt"
    params = loadobj(path)
    a = params[0]
    #a['struct'] = "@16500%2@17400%1@20000%0"
    #a['fold'] = True
    a['paint'] = 100
    b = params[1]
    #b['fold'] = True
    code = demjson.encode(params)
    print(code)
    writeline(path, code)

main()
