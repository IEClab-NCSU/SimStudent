

from watson_developer_cloud import NaturalLanguageUnderstandingV1
from watson_developer_cloud.natural_language_understanding_v1 \
  import Features, KeywordsOptions

# Watson Natural Language Understanding API credentials
nlu = NaturalLanguageUnderstandingV1(
  username="5eecf638-b669-42a5-9c95-8e39ab31570f",
  password="e4lZ2UnuB0VE",
  version="2017-02-27")


def get_skillnames(clusters, iterative_clustering):
    cluster_labels = []
    num = 1
    for cluster in clusters:

        try:
            # Using the Watson Natural Language Understanding service
            response = nlu.analyze(
                text=cluster,
                features=Features(
                    keywords=KeywordsOptions(
                        limit=2)
                )
            )
            words = []
            for keyword in response["keywords"]:
                words.append(keyword["text"].encode('utf-8'))

        except:
            cluster_labels.append(['noname'+str(num)])
            num = num + 1
            continue
        if not words:
            cluster_labels.append(['noname'+str(num)])
            num = num + 1
            continue

        cluster_labels.append(words)

    print "The cluster labels are..."

    '''for index, label in enumerate(cluster_labels):
        print str(index+1) + ". " + str(label[:2])
    '''

    return 0, cluster_labels


    """
    if not iterative_clustering:
        return 0, cluster_labels

    merged = []
    for index, label in enumerate(cluster_labels):
        if index in merged:
            continue
        if label[0] == '':
            merged.append(index)
        for index2, label2 in enumerate(cluster_labels[index+1:]):
            if index2 in merged:
                continue
            if len(set(label[:2]).intersection(label2[:2])) >= 2:
                merged.append(index2)d

    if len(merged) > 0:
        print "\n"+str(len(merged)) + " clusters are being merged..."

    new_num_clusters = len(cluster_labels)-len(merged)

    return new_num_clusters, cluster_labels
    """
