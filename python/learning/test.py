import matplotlib.font_manager as fm

# 获取系统中可用的字体列表
font_list = fm.findSystemFonts()

# 打印字体列表
for font_path in font_list:
    print(font_path)