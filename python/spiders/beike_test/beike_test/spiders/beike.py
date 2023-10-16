import logging
import re
from datetime import datetime, timedelta

import requests
import scrapy
from scrapy import Request

from spiders.beike_test.beike_test.items import BeikeTestItem


class BeikeSpider(scrapy.Spider):
    name = "beike"
    allowed_domains = ["tj.ke.com"]
    start_url = "https://tj.ke.com//ershoufang/{}/pg{}dp1"
    headers = {
        'User-Agent': 'Mozilla/5.0'
    }

    def __init__(self,**kwargs):
        self.street_name=kwargs['street_name']
        self.area_name=kwargs['area_name']
        self.url_street_name = kwargs['url_street_name']
        self.pages = int(kwargs['pages'])

    # 启动时设置所有请求页
    def start_requests(self):
        for i in range(self.pages):
            url = self.start_url.format(self.url_street_name,i + 1)
            # ,meta={'proxy': self.get_proxy()}
            yield Request(url=url, callback=self.parse, errback=self.errback, meta={'proxy': 'socks5://localhost:7890'})

    def get_proxy(self):
        # API_url为您在网站上的API
        API_url = 'http://api.3ip.cn/dmgetip.asp?apikey=9035be48&pwd=4b4d682328198740223d46470a582250&getnum=1&httptype=0&geshi=1&fenge=1&fengefu=&Contenttype=1&operate=all'
        if not API_url:
            logging.error('出错了:请从www.3ip.cn生成api地址')
            proxy = {}
            return proxy
        try:
            aaa = requests.get(API_url, headers=self.headers).text
            if not aaa or 'code' in aaa:
                proxy = {}
            else:
                proxy_host = aaa.splitlines()[0]
                print('代理IP为：' + proxy_host)
                proxy = 'socks5://' + proxy_host
                # proxy = {
                #     'http': 'socks5://' + proxy_host,
                #     'https': 'socks5://' + proxy_host,
                # }
        except Exception as e:
            print('获取代理IP失败：', e)
            proxy = {}
        return proxy

    def sub_date(self, text):
        today = datetime.now()
        date_model = text[len(text) - 1]
        num_str = text[0:len(text) - 1]
        if "年" in date_model:
            new_date = today - timedelta(days=365 * int(num_str))
        elif "月" in date_model:
            new_date = today - timedelta(days=30 * int(num_str))
        elif "日" in date_model:
            new_date = today - timedelta(days=int(num_str))
        else:
            new_date = today
        return new_date.strftime("%Y-%m-%d")

    def parse(self, response):
        li_list = response.xpath("//ul[@class='sellListContent']//li[@class='clear']")
        for li in li_list:
            item = BeikeTestItem()
            href = li.xpath("./a/@href").extract_first()
            item['bei_ke_id'] = int(re.findall(r'\d+', href)[0])
            item['cell_name'] = li.xpath(".//div[@class='positionInfo']/a/text()").extract_first()
            item['area_name'] = self.area_name
            item['street_name']=self.street_name
            pub_str = li.xpath(".//div[@class='followInfo']/text()").extract()[1]
            new_pub_str = pub_str[pub_str.find("/") + 1:pub_str.find("前")]

            item['pub_date_str'] = self.sub_date(new_pub_str)
            house_desc_info = li.xpath(".//div[@class='houseInfo']/text()").extract()[1]
            house_infos = re.sub(r'[\s]+', '', house_desc_info.strip()).split("|")
            item['floor_desc'] = house_infos[0][0:house_infos[0].find('层') + 1]
            item['total_floor_num'] = int(
                house_infos[0][house_infos[0].find('共') + 1:house_infos[0].rfind('层')].strip())
            room_num_desc = house_infos[0][house_infos[0].find(')') + 1:len(house_infos[0])]
            item['bedroom_num'] = int(room_num_desc[0:room_num_desc.find('室')])
            item['live_room_num'] = int(room_num_desc[room_num_desc.find('室') + 1:room_num_desc.find('厅')])
            item['room_area'] = house_infos[1][0:house_infos[2].find("平米") - 1].strip()
            item['chao_xiang'] = house_infos[2].strip()
            item['total_price'] = li.xpath(".//div[@class='totalPrice totalPrice2']//span/text()").extract_first()
            item['tags'] = ",".join(li.xpath('./div/div[2]/div[4]/span/text()').extract())
            yield item
            # yield Request(
            #     url=href,
            #     callback=self.parse_detail,
            #     cb_kwargs={'item': item}
            # )

    # def parse_detail(self, response, **kwargs):
    #     item = kwargs['item']
    #     # 总价
    #     item['total_price'] = response.xpath("//span[@class='total']/text()").extract_first()
    #     # 2室1厅;北;61.85平米
    #     main_infos = response.xpath("//div[@class='mainInfo']/text()").extract()
    #     item['room_info_str'] = main_infos[0]
    #     item['room_layout'] = main_infos[1]
    #     item['room_area'] = main_infos[2][0:main_infos[2].find("平米")].strip()
    #     # 贝壳id
    #     item['beike_id'] = re.findall(r'\d+',
    #                    response.xpath("//div[@class='houseRecord']//span[@class='info']/text()").extract_first())[0]
    #     gua_pai_date_str = response.xpath("//div[@class='transaction']//li[1]/text()").extract_first().replace(' ',
    #                                                                                                            '').replace(
    #         '\n', '')
    #     item['gua_pai_date'] = datetime.strptime(gua_pai_date_str, '%Y年%m月%d日')
    #     transaction_date_str = response.xpath("//div[@class='transaction']//li[3]/text()").extract_first().replace(' ',
    #                                                                                                                '').replace(
    #         '\n', '')
    #     item['transaction_date'] = datetime.strptime(transaction_date_str, '%Y年%m月%d日')
    #     item['cell_name'] = response.xpath("//div[@class='communityName']//a[1]/text()").extract_first()
    #     item['area_name'] = response.xpath("//div[@class='areaName']//a[1]/text()").extract_first()
    #     item['street_name'] = response.xpath("//div[@class='areaName']//a[2]/text()").extract_first()
    #     item['room_age'] = response.xpath("//div[@class='transaction']//li[5]/text()").extract_first()
    #     yield item

    def errback(self, failure):
        self.logger.error(repr(failure))
