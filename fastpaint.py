import matplotlib.pyplot as plt 
import numpy as np
import os
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
    x = []
    y = []
    x, y = getXY(basepath + "function.txt", 1000)

    '''a=np.polyfit(x[-recentnum:-1],y[-recentnum:-1],risklevel)#用2次多项式拟合x，y数组
    b=np.poly1d(a)#拟合完之后用这个函数来生成多项式对象
    c=b(x[-recentnum:-1])#生成多项式对象之后，就是获取x在这个多项式处的值'''
    fig = plt.figure(figsize=(5, 2.5))#, top=0, right=1)
    #plt.scatter(x,y,marker='.',label='raw')#对原始数据画散点图
    plt.plot(x,y,color="black")
    plt.scatter(x,y,marker='.',label="")#对近期拟合画散点图
    #plt.plot(x[-recentnum:-1],c,ls='--',c='red',label='fitting')#对拟合之后的数据，也就是x，c数组画图
    plt.legend()
    # plt.margins(x=0, y=0)
    plt.draw()
    plt.pause(10)
    plt.close(fig)

main()
