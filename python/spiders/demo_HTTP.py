# -*- coding: cp936 -*-
import requests
#测试python版本为：python3.8.5
#如果提示requests出错，可能需要安装request 运行 pip install requests
#安装request库时,请不要用国内源,防止安装版本与python版本不匹配
def get_proxy(headers):
    #API_url为您在网站上的API
    API_url = 'http://api.3ip.cn/dmgetip.asp?apikey=9035be48&pwd=4b4d682328198740223d46470a582250&getnum=1&httptype=0&geshi=1&fenge=1&fengefu=&Contenttype=1&operate=all'
    if not API_url:
        print('出错了:请从www.3ip.cn生成api地址')
        proxy = {}
        return proxy
    try:
        aaa = requests.get(API_url, headers=headers).text
        if not aaa or 'code' in aaa:
            proxy = {}
        else:
            proxy_host = aaa.splitlines()[0]
            print('代理IP为：'+proxy_host)
            proxy = {
                'http': 'socks5://'+proxy_host,
                'https': 'socks5://'+proxy_host,
            }
    except Exception as e:
        print('获取代理IP失败：', e)
        proxy = {}
    return proxy


if __name__ == '__main__':
    
    url = 'https://tj.ke.com/ershoufang/101120965519.html?fb_expo_id=755413251285692416'
    headers = {
        'User-Agent': 'Mozilla/5.0'
    }

    proxy = get_proxy(headers)
    if not proxy:
        print('获取代理失败,停止继续执行')
    else: 
        print('HTTPS测试')
        requests.packages.urllib3.disable_warnings()#关闭HTTPS校验
        try:
            r = requests.get(url, headers=headers, proxies=proxy, verify=False)
            print(r.text)
        except:
            print('代理使用失败，请更换代理IP')
