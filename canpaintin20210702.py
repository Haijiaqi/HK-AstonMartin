import matplotlib.pyplot as plt 
import numpy as np
import time
def main():
    fp = open("D:\\a\\temp\\20210702t\\pythonparam.txt", 'r')
    for aline in fp:
        params = aline.split(';')
        path = params[0].split('\\')
        path = '\\\\'.join(path)
        maxi = eval(params[1])
        risklevel = eval(params[2])
        recentnum = eval(params[3])
        tab = params[4]
    fp.close()
    outsize = 1200
    x = []
    y = []
    m = []
    n = []
    e = []
    f = []
    p = []
    q = []
    v = []
    w = []
    s = []
    t = []
    fo = open(path, 'r')
    i = 0
    for aline in fo:
        if i == 0:
            startX = eval(aline.split(',')[0])
        try:
            code = aline.split(',')#1595779200#1593360000#
            x.append((eval(code[0]) - startX) / (864000 * 10))
            y.append(eval(code[1]))
            i += 1
            if i == maxi:
                break
        except:
            print("原始数据")
    fo.close()
    fa = open("D:\\a\\polynomial_all.txt", 'r')
    i = 0
    for aline in fa:
        try:
            code = aline.split(',')
            m.append(eval(code[0]))
            n.append(eval(code[1]))
            i += 1
            if i == maxi:
                break
        except:
            print("全局稳健拟合")
    fa.close()
    fr = open("D:\\a\\temp\\20210702t\\rawpoints.txt", 'r')
    i = 0
    for aline in fr:
        try:
            code = aline.split(',')
            e.append(eval(code[0]))
            f.append(eval(code[1]))
            i += 1
            if i == recentnum:
                break
        except:
            print("近期拟合")
    fr.close()
    fq = open("D:\\a\\temp\\20210702t\\actionpointss.txt", 'r')
    i = 0
    for aline in fq:
        try:
            code = aline.split(',')
            p.append(eval(code[0]))
            q.append(eval(code[1]))
            i += 1
            if i == recentnum:
                break
        except:
            print("二次拟合")
    fq.close()
    frn = open("D:\\a\\temp\\20210702t\\quadratic.txt", 'r')
    i = 0
    for aline in frn:
        try:
            code = aline.split(',')
            v.append(eval(code[0]))
            w.append(eval(code[1]))
            i += 1
            if i == outsize:
                break
        except:
            print("近期外推")
    frn.close()
    fqn = open("D:\\a\\temp\\20210702t\\actionpoints.txt", 'r')
    i = 0
    for aline in fqn:
        try:
            code = aline.split(',')
            s.append(eval(code[0]))
            t.append(eval(code[1]))
            i += 1
            if i == outsize:
                break
        except:
            print("二次外推")
    fqn.close()
    a=np.polyfit(x[-recentnum:-1],y[-recentnum:-1],risklevel)#用2次多项式拟合x，y数组
    b=np.poly1d(a)#拟合完之后用这个函数来生成多项式对象
    c=b(x[-recentnum:-1])#生成多项式对象之后，就是获取x在这个多项式处的值
    plt.figure(figsize=(10, 5))#, top=0, right=1)
    #plt.scatter(x,y,marker='.',label='raw')#对原始数据画散点图
    plt.plot(x,y,color="black")
    plt.scatter(e,f,marker='.',label=tab)#对近期拟合画散点图
    plt.scatter(s,t,marker='.')#,label='s')#对二次外推画散点图
    plt.scatter(v,w,marker='x')#,label='v')#对近期外推画散点图
    plt.scatter(p,q,marker='.')#,label='p')#对二次拟合画散点图
    plt.scatter(m,n,marker='+')#,label='n')#对二次外推画散点图
    #plt.plot(x[-recentnum:-1],c,ls='--',c='red',label='fitting')#对拟合之后的数据，也就是x，c数组画图
    plt.legend()
    # plt.margins(x=0, y=0)
    plt.show()

main()
