"""
Main Function and Preprocessing

"""

from get_Data_from_SQL import get_content
from preprocessing import preprocess
from getSkills import create_mapping
from paragraph_tagging import find_similarity
from insertStandardReferences import insert_TEKS
from get_Standard_data import get_Standard_data


def main():
    paragraphs, questions = get_content()

    """
    paragraphs = []
    questions = []
    with open('Science Course SQL.csv', 'r') as csvfile:
        csvreader = csv.reader(csvfile)
        for row in csvreader:
            if row[6] == "TextParagraph":
                paragraphs.append([row[0], row[9]])
            if row[6] == "TextBoxQuestion":
                questions.append([row[0], row[10]])
    """

    paragraphs = preprocess(paragraphs)
    questions = preprocess(questions)

    num_clusters = 8

    # Find Clusters
    paragraphs_to_skills, clusters, questions_to_skills, skillnames = \
        create_mapping(paragraphs, questions, num_clusters,
                       iterative_clustering=False)

    # Get Standard IDs and descriptions
    fileName = "Science_teks_references.csv"
    teks_names, teks_paragraphs = get_Standard_data(fileName)

    # Find Cosine Similarity between Paragraphs and TEKS
    questions_to_teks, paragraphs_to_teks = find_similarity(clusters, paragraphs_to_skills, questions_to_skills,
                                            skillnames, teks_paragraphs, teks_names)

    # insert_TEKS(paragraphs_to_teks, questions_to_teks)


if __name__ == '__main__':
    main()
