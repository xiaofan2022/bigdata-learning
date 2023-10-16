# -*- coding: cp936 -*-
import requests
#����python�汾Ϊ��python3.8.5
#�����ʾrequests����������Ҫ��װrequest ���� pip install requests
#��װrequest��ʱ,�벻Ҫ�ù���Դ,��ֹ��װ�汾��python�汾��ƥ��
def get_proxy(headers):
    #API_urlΪ������վ�ϵ�API
    API_url = 'http://api.3ip.cn/dmgetip.asp?apikey=9035be48&pwd=4b4d682328198740223d46470a582250&getnum=1&httptype=0&geshi=1&fenge=1&fengefu=&Contenttype=1&operate=all'
    if not API_url:
        print('������:���www.3ip.cn����api��ַ')
        proxy = {}
        return proxy
    try:
        aaa = requests.get(API_url, headers=headers).text
        if not aaa or 'code' in aaa:
            proxy = {}
        else:
            proxy_host = aaa.splitlines()[0]
            print('����IPΪ��'+proxy_host)
            proxy = {
                'http': 'socks5://'+proxy_host,
                'https': 'socks5://'+proxy_host,
            }
    except Exception as e:
        print('��ȡ����IPʧ�ܣ�', e)
        proxy = {}
    return proxy


if __name__ == '__main__':
    
    url = 'https://tj.ke.com/ershoufang/101120965519.html?fb_expo_id=755413251285692416'
    headers = {
        'User-Agent': 'Mozilla/5.0'
    }

    proxy = get_proxy(headers)
    if not proxy:
        print('��ȡ����ʧ��,ֹͣ����ִ��')
    else: 
        print('HTTPS����')
        requests.packages.urllib3.disable_warnings()#�ر�HTTPSУ��
        try:
            r = requests.get(url, headers=headers, proxies=proxy, verify=False)
            print(r.text)
        except:
            print('����ʹ��ʧ�ܣ����������IP')
