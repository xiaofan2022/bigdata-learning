import time

from selenium import webdriver
from selenium.webdriver.common.by import By

options = webdriver.ChromeOptions()
#保持
#options.add_experimental_option("detach", True)
#1.创建Chrome或Firefox浏览器对象，这会在电脑上在打开一个浏览器窗口
driver = webdriver.Chrome(options=options)

#2.通过浏览器向服务器发送URL请求。如果能打开百度网站，说明安装成功。
driver.get("https://www.baidu.com/")
input_button=driver.find_element(By.CSS_SELECTOR,"#kw")
input_button.send_keys("你好")
time.sleep(1)
query_button=driver.find_element(By.CSS_SELECTOR,"#su")
query_button.click()
time.sleep(10)
driver.quit()