import matplotlib.pyplot as plt 
import numpy as np
import os
import demjson
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


def main():
    basepath = os.getcwd() + '/work/coin/paint/'
    params = loadobj(basepath + "processInfo.txt")
    print(basepath)
    tab, path = loadparam(basepath + "pythonparam.txt")
    x = []
    y = []
    x, y = getXY(basepath + "rawdata.txt", 1000)
    fitx = []
    fity = []
    fitx, fity = getXY(basepath + "polynomial.txt", 1000)
    cubicx = []
    cubicy = []
    cubicx, cubicy = getXY(basepath + "cubic.txt", 1000)
    quatraticx = []
    quatraticy = []
    quatraticx, quatraticy = getXY(basepath + "quatratic.txt", 1000)
    weightupx = []
    weightupy = []
    weightupx, weightupy = getXY(basepath + "weightup.txt", 1000)
    weightdnx = []
    weightdny = []
    weightdnx, weightdny = getXY(basepath + "weightdn.txt", 1000)

    '''a=np.polyfit(x[-recentnum:-1],y[-recentnum:-1],risklevel)#用2次多项式拟合x，y数组
    b=np.poly1d(a)#拟合完之后用这个函数来生成多项式对象
    c=b(x[-recentnum:-1])#生成多项式对象之后，就是获取x在这个多项式处的值'''
    fig = plt.figure(figsize=(4, 2))#, top=0, right=1)
    #plt.scatter(x,y,marker='.',label='raw')#对原始数据画散点图
    plt.plot(x,y,color="black")
    plt.scatter(fitx,fity,marker='.',label=tab)#对近期拟合画散点图
    plt.scatter(cubicx,cubicy,marker='.')#,label='s')#对二次外推画散点图
    plt.scatter(quatraticx,quatraticy,marker='+')#,label='v')#对近期外推画散点图
    plt.scatter(weightupx,weightupy,marker='.')#,label='p')#对二次拟合画散点图
    plt.scatter(weightdnx,weightdny,marker='.')#,label='n')#对二次外推画散点图
    #plt.plot(x[-recentnum:-1],c,ls='--',c='red',label='fitting')#对拟合之后的数据，也就是x，c数组画图
    plt.legend()
    # plt.margins(x=0, y=0)
    plt.draw()
    plt.pause(params['st'] - 2)
    plt.close(fig)

main()
