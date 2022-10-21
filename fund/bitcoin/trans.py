import demjson
import os
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
        if stamp > 1666017550:
        break'''
        fi.write(aline + '\n')
    fi.close()
    #print(result["data"]["body"]["data"]["points"]["1665414600"]["v"][4])
    return result
text = getparam(0)
