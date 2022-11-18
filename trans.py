import demjson
import os
import requests
import time
import json

import okx.Account_api as Account
import okx.Funding_api as Funding
import okx.Market_api as Market
import okx.Public_api as Public
import okx.Trade_api as Trade
import okx.status_api as Status
import okx.subAccount_api as SubAccount
import okx.TradingData_api as TradingData
import okx.Broker_api as Broker
import okx.Convert_api as Convert
import okx.FDBroker_api as FDBroker
import okx.Rfq_api as Rfq
import okx.TradingBot_api as TradingBot
import okx.Finance_api as Finance

if __name__ == '__main__':
    api_key = "9df9b09b-3ca0-4184-9f33-970cabdffa9e"
    secret_key = "1EF3E974E3F46008BF80AC22129322CA"
    passphrase = "OKEx@155818"
    # flag是实盘与模拟盘的切换参数 flag is the key parameter which can help you to change between demo and real trading.
    flag = '1'  # 模拟盘 demo trading
    # flag = '0'  # 实盘 real trading

    # trade api
    tradeAPI = Trade.TradeAPI(api_key, secret_key, passphrase, False, flag)
    basepath = os.getcwd() + '/work/coin/'
    xchangeout = basepath + "xchangeout"
    xchangein = basepath + "xchangein"
    print("started...")
    while True:
        list = os.listdir(xchangeout)
        i = 0
        for line in list:
            text = line.split(",")
            ts = eval(text[0])
            nowtime = time.time() * 1000
            ifdeal = False
            if nowtime - ts < 5000 and nowtime - ts > 0:
                # 下单  Place Order
                try:
                    result = tradeAPI.place_order(instId=text[1], tdMode=text[2], side=text[3], 
                    # posSide='short',
                                                ordType=text[4], sz=text[5])
                    # tgtCcy='',banAmend='')
                    # 获取订单信息  Get Order Details
                    # time.sleep(0.1)
                    obj = result
                    returncode = obj['data'][0]['sCode']
                    returnmsg = obj['data'][0]['sMsg']
                    if returncode == '0':
                        if text[3] == 'buy':
                            returnordId = obj['data'][0]['ordId']
                            result = tradeAPI.get_orders(text[1], returnordId)#-USDT-201225
                            returnavgPx = result['data'][0]['avgPx']
                            if '' == returnavgPx:
                                print('strange!')
                            returnString = text[0] + "," + returnordId + "," + returnavgPx + "," + returncode + "," + returnmsg + ":" + returnavgPx
                        else:
                            returnordId = obj['data'][0]['ordId']
                            result = tradeAPI.get_orders(text[1], returnordId)#-USDT-201225
                            returnavgPx = result['data'][0]['avgPx']
                            if '' == returnavgPx:
                                print('strange!')
                            returnString = text[0] + "," + returnordId + "," + returnavgPx + "," + returncode + "," + returnmsg + ":" + returnavgPx
                        ifdeal = True
                    else:
                        if returncode == '50013' or returncode == '50001':
                            ifdeal = False
                            time.sleep(0.5)
                        else:
                            ifdeal = True
                        returnString = text[0] + "," + 'orgId' + "," + "tag" + "," + returncode + "," + returnmsg
                    print(line + "\nfileNETdone!" + returnString)
                except:
                    print(line + "\nsomething wrong!")
                    ifdeal = False
            else:
                nowtime = time.time() * 1000
                if ts - nowtime > 100:
                    ifdeal = False
                else:
                    returnString = text[0] + "," + "ordId" + "," + "tag" + "," + "-4" + "," + "exceed5s"
                    print(line + "\nEXCEED!" + str(nowtime) + " " + returnString)
                    ifdeal = True
            if ifdeal:
                fi = open(xchangein + "/" + returnString, 'w')
                fi.close()
                abspath = xchangeout + "/" + line
                if os.path.exists(abspath):
                    os.remove(abspath)
            time.sleep(0.1)
            i += 1
