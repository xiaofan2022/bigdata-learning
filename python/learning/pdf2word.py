import os
from pdf2docx import Converter

def pdf_to_word(input_pdf_path, output_word_path):
    try:
        # 初始化转换器
        cv = Converter(input_pdf_path)
        # 执行转换
        cv.convert(output_word_path, start=0, end=None)
        # 关闭转换器
        cv.close()
    except Exception as e:
        print(f"Error during conversion: {e}")

#修改constants.py DEFAULT_FONT_NAME = 'helv'
if __name__ == "__main__":
    # 输入的 PDF 文件路径
    input_pdf_path = "E:\BaiduNetdiskDownload\大数据面试题\大数据面试题.pdf"
    # 输出的 Word 文件路径
    output_word_path = "E:\BaiduNetdiskDownload\大数据面试题\数据面试题.docx"
    # 检查输入的 PDF 文件是否存在
    if not os.path.exists(input_pdf_path):
        print(f"Error: The PDF file '{input_pdf_path}' does not exist.")
        exit(1)
    # 执行 PDF 到 Word 的转换
    pdf_to_word(input_pdf_path, output_word_path)
    print(f"Successfully converted '{input_pdf_path}' to '{output_word_path}'.")

