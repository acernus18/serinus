import os
import re
import sys

import requests


def renameFileByPattern():
    pattern = r'^.*(IMG_2014.*\.jpg)$'
    for item in os.listdir('.'):
        matchResult = re.match(pattern, item)
        if matchResult:
            print('{} -> {}'.format(item, matchResult.group(1)))
        # os.rename(item, matchResult.group(1))
        else:
            print(item + ' do nothing')


def clearTempFile(tempFilePath):
    for item in os.listdir(tempFilePath):
        print("Clearing {}{}".format(tempFilePath, item))
        os.remove(tempFilePath + item)
    print('Done')


def generateName(index: int):
    result = str(index)
    if len(result) < 5:
        result = '0' * (5 - len(result)) + result

    return result


def downloadByChunk(url, tempFilePath):
    matchResult = re.match(r'^.*/([^/]+)$', url)

    headers = {
        "Cookie": "JSESSIONID=50B1270C8C4A3EAA804893163F928E4A",
        "Referer": "http://162.105.134.188/pdfindex.jsp?fid=15c19e02ab13ac80cfa2bc28ccb88589&xyz=0.91559873586277",
        "Connection": "keep-alive",
        "Host": "162.105.134.188",
        "Accept": "image/webp,image/apng,image/*,*/*;q=0.8"
    }

    if matchResult:
        filename = matchResult.group(1)
        print("Downloading {}".format(filename))
        response = requests.get(url, stream=True, headers=headers)
        print('Result = ' + str(response.status_code))
        outputFile = open(tempFilePath + filename, 'wb')
        for chunk in response.iter_content(chunk_size=1024):
            outputFile.write(chunk)
        outputFile.close()
        print("Done")
    else:
        print('Cannot match filename for url = {}'.format(url))


def main():
    print('HTTP help script running ...')
    print('Current working directory: ' + os.getcwd())
    print('System parameters = ' + str(sys.argv))

    tempFilePath = "D:/maple/Documents/temp/"

    if sys.argv[1] == 'clear':
        clearTempFile(tempFilePath)
    else:
        for i in range(60, 73):
            url = 'http://162.105.134.188/store/CCRIfA94j6CypAannIc4jg11/P01_' + generateName(i + 1) + ".jpg"
            downloadByChunk(url, tempFilePath)


if __name__ == '__main__':
    main()
