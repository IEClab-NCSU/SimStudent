#!C:/UsrLocal/Perl/bin/perl.exe
# -*- perl -*-

# Variables
#

$cvs_dir = "f:/Project/CTAT/CVS-TREE";
$DStoBRD_dir = "$cvs_dir/AuthoringTools/java/source/edu/cmu/pact/miss/DStoBRD";
$data_dir = "$DStoBRD/data/Wilkinsburg-2005-AlgebraI";
$data_file = "$data_dir/filtered.txt";

$output_file = "$data_dir/brd_input.txt";

# ----------------------------------------------------------------------
# Top level routine
# ----------------------------------------------------------------------

# Initialize the output file
init_output_file();

