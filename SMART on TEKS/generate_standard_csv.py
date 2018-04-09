import csv
from bs4 import BeautifulSoup


def generate_csv_from_txt():
    with open("teks standard.txt", 'r') as file:
        teks_file = file.read()

    def loopUntilA(pTag, paraText, count, skill):
        if pTag and pTag.name == "p" and pTag.has_attr("class") and pTag["class"][0] == "SUBPARAGRAPHA":
            text = pTag.text
            text = ''.join([i if ord(i) < 128 else ' ' for i in text])
            teks_paragraphs.append(paraText + " " + text)
            teks_names.append(str(7) + "(" + str(count) + ")" + "(" + chr(ord(skill)) + ")")
            skill = chr(ord(skill) + 1)

        pTag = pTag.find_next()

        if (not pTag) or (pTag.has_attr("class") and pTag["class"][0] == "PARAGRAPH1"):
            return
        else:
            # Using double next to skip the string nodes themselves
            loopUntilA(pTag, paraText, count, skill)
            return

    html = BeautifulSoup(teks_file)
    pTags = html.find_all("p", {"class": "PARAGRAPH1"})
    teks_paragraphs = []
    teks_names = []
    count = 1

    for pTag in pTags:
        paraText = pTag.text
        paraText = ''.join([i if ord(i) < 128 else ' ' for i in paraText])
        pTag = pTag.findNext()
        loopUntilA(pTag, paraText, count, 'A')
        count += 1

    teks_references = []

    for index in range(len(teks_paragraphs)):
        teks_references.append([teks_names[index], teks_paragraphs[index]])

    with open("Science_teks_references.csv", 'w') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerows(teks_references)
