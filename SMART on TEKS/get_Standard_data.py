import csv


def get_Standard_data(fileName):

    standardID = []
    standardDescription = []

    with open(fileName, 'r') as csvfile:
        csvreader = csv.reader(csvfile)
        for row in csvreader:
            standardID.append(row[0])
            standardDescription.append(row[1])

    return standardID, standardDescription
