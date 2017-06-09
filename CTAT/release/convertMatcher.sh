#!/bin/bash

# Change an old-style <matcher> elements in .BRD into a new-style <matchers> elements.

if test $# -lt 1 -o ! -r "$1"; then
    printf "
%s: Input file unspecified or unreadable. Usage:
  %s input.brd > output.brd
where:
  input.brd   is the file to alter;
  output.brd  is the output file (writes to stdout).
" $(basename $0) $(basename $0) 
    exit 2
fi

# Determine whether lines end with CRLF or just LF.
insertCR='s/someJunkNeverFoundSoThisIsANoop//'
if head -3 "$1" | cat -tev | grep -qF ^M; then
	insertCR='s/$/\r/'
fi

# Do the real work

awk '
    function printSAI(elt, type, param) {
		printf("<%s>\n                    ", elt);
		printf("<matcher>\n                        ");
		printf("<matcherType>%s</matcherType>\n                        ", type);
		printf("<matcherParameter name=\"single\">%s</matcherParameter>\n                    ", param);
		printf("</matcher>\n                ");
		printf("</%s>\n                ", elt);
    }
    #
    # Print a new-style <matchers> element.
    #
    function printMatcher(e) {
		printf("<matchers Concatenation=\"true\">\n                ");
		printSAI("Selection", matcherType[e], selection[e]);
		printSAI("Action", matcherType[e], action[e]);
		printSAI("Input", matcherType[e], input[e]);
		printf("<Actor linkTriggered=\"false\">%s</Actor>\n            ", actor[e]);
		printf("</matchers>");
    }
    #
    # Parse an <edge> element to extract its matcher parameters into element [e]
    # of their respective arrays. Preserve the rest of the element in the edgeText* arrays.
    function recordEdge(edgeText, e) {
        wholeEdge[e] = "";
        if(match(edgeText, "<matchers +Concatenation") > 0) {
            wholeEdge[e] = edgeText;
            return;
        }
        nf = split(edgeText, EDGE, "</?uniqueID>|</?matcher>|</?matcherType>|<matcherParameter name=\"selection\">|<matcherParameter name=\"action\">|<matcherParameter name=\"input\">|<matcherParameter name=\"actor\">|</matcherParameter>");
        if(nf < 15) {
            printf("\nline %d error: could not parse edge; nf=%d\n", NR, nf);
        } else {
            edgeText1[e] = sprintf("%s<uniqueID>", EDGE[1]);
            edgeID[e] = EDGE[2];
            edgeText2[e] = sprintf("</uniqueID>%s", EDGE[3]);
            matcherType[e] = EDGE[5];
			selection[e] = EDGE[7];
			action[e] = EDGE[9];
			input[e] = EDGE[11];
			actor[e] = EDGE[13];
			# printf("[%2d] text1 %s, id %s, matcherType=%s, S=%-18s, A=%-17s, I=%s, actor=%s\n", e, edgeText1[e], edgeID[e], matcherType[e], selection[e], action[e], input[e], actor[e]);
            edgeText3[e] = EDGE[15];
        }
    }
    #
    # Print an <edge> element from the edge* arrays.
    #
    function printEdge(en) {
        if(wholeEdge[en] != "") {
            printf("%s\n", wholeEdge[en]);
        } else {
            printf("%s%s%s", edgeText1[en], edgeID[en], edgeText2[en]);
            printMatcher(en);
            printf("%s\n", edgeText3[en]);
        }
    }
    #
    # Execution begins here.
    #
    BEGIN         {
                    nNodes = 0; nEdges = 0; maxNodeID = -1; maxStateNo = -1; maxY = -1; maxEdgeID = -1;
                    a = 0;
                    ALPHA[++a]="A"; ALPHA[++a]="B"; ALPHA[++a]="C"; ALPHA[++a]="D"; ALPHA[++a]="E"; ALPHA[++a]="F";
                    ALPHA[++a]="G"; ALPHA[++a]="H"; ALPHA[++a]="I"; ALPHA[++a]="J"; ALPHA[++a]="K"; ALPHA[++a]="L";
                    ALPHA[++a]="M"; ALPHA[++a]="N"; ALPHA[++a]="O"; ALPHA[++a]="P"; ALPHA[++a]="Q"; ALPHA[++a]="R";
                    ALPHA[++a]="S"; ALPHA[++a]="T"; ALPHA[++a]="U"; ALPHA[++a]="V"; ALPHA[++a]="W"; ALPHA[++a]="X";
                    ALPHA[++a]="Y"; ALPHA[++a]="Z";
    }
    $1 == "<edge>" {                  # concatenate the edge element into a single line
                    lastEdge = $0
                    do {
                        getline;
                        lastEdge = sprintf("%s\n%s", lastEdge, $0);
                    } while($1 != "</edge>");
                    recordEdge(lastEdge, ++nEdges);   # parse and get the changeable parts
                    printEdge(nEdges);
                    next;
    }
                   {                                 # print anything that is not a node or edge
                     print;
    }
' "$1" | \
sed "$insertCR"
