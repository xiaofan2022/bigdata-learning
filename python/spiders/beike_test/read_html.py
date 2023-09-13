import re

from bs4 import BeautifulSoup

if __name__ == '__main__':
    soup = BeautifulSoup(open("D:\\data\\test\\beike.html", 'r', encoding='utf-8'), 'html.parser')
    price_container = soup.select("div.price-container")
    #总价
    total_price=price_container[0].select_one("div.price >span").text
    main_info=soup.select("div.mainInfo")
    #2室1厅;北;61.85平米
    main_info_str = ';'.join([element.text for element in main_info])
    id_str=soup.select('div.houseRecord')[0].select_one('span.info').text
    #贝壳id
    id = re.findall(r'\d+', id_str)
    gua_pai_date=soup.select("#introduction > div > div > div.transaction > div.content > ul > li:nth-child(1)")[0].text
    transaction_date=soup.select("#introduction > div > div > div.transaction > div.content > ul > li:nth-child(3)")[0].text
    cell_name=soup.select('#beike > div.sellDetailPage > div:nth-child(6) > div.overview > div.content > div.aroundInfo > div.communityName > a.info.no_resblock_a')[0].text
    area_name=soup.select('#beike > div.sellDetailPage > div:nth-child(6) > div.overview > div.content > div.aroundInfo > div.areaName > span.info > a:nth-child(1)')[0].text
    street_name=soup.select('#beike > div.sellDetailPage > div:nth-child(6) > div.overview > div.content > div.aroundInfo > div.areaName > span.info > a:nth-child(2)')[0].text

    print(main_info_str)