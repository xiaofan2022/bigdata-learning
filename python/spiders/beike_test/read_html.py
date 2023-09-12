import re

from bs4 import BeautifulSoup

if __name__ == '__main__':
    soup = BeautifulSoup(open("D:\\data\\test\\beike.html", 'r', encoding='utf-8'), 'html.parser')
    price_container = soup.select("div.price-container")
    total_price=price_container[0].select_one("div.price >span").text
    main_info=soup.select("div.mainInfo")
    main_info_str = ';'.join([element.text for element in main_info])
    id_str=soup.select('div.houseRecord')[0].select_one('span.info').text
    id = re.findall(r'\d+', id_str)

    print(main_info_str)