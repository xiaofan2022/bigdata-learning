from lxml import etree
html = etree.parse('D:\\data\\test\\index.html', etree.HTMLParser())
li_list = html.xpath("//*[@id='beike']//ul/li[@class='clear']")
for li in li_list:
    print(li.xpath("./a/@href"))