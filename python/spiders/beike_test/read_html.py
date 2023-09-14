import re

from lxml import etree

if __name__ == '__main__':
    html = etree.parse('D:\\data\\test\\beike.html', etree.HTMLParser())
    # 总价
    total_price = html.xpath("//span[@class='total']/text()")
    main_infos = html.xpath("//div[@class='mainInfo']/text()")
    # # 2室1厅;北;61.85平米
    room_info_str = main_infos[0]
    room_layout = main_infos[1]
    room_area = main_infos[2]
    id_str = html.xpath("//div[@class='houseRecord']//span[@class='info']/text()")
    # # 贝壳id
    id = re.findall(r'\d+', id_str[0])
    gua_pai_date = html.xpath("//div[@class='transaction']//li[1]/text()")
    transaction_date =html.xpath("//div[@class='transaction']//li[3]/text()")
    cell_name=html.xpath("//div[@class='communityName']//a[1]/text()")
    area_name=html.xpath("//div[@class='areaName']//a[1]/text()")
    street_name =html.xpath("//div[@class='areaName']//a[2]/text()")
    room_age=html.xpath("//div[@class='transaction']//li[5]/text()")
    print("qqqqqq")
