
open (LOG, "TabbedPreTest.java") || die "AWWWWWWWWWWWWWWWW!"; 
open (NewA, "> TabbedPreTestA.java") || die "AWWWWWWWWWWWWWWWW!"; 
open (NewB, "> TabbedPreTestB.java") || die "AWWWWWWWWWWWWWWWW!"; 
while(<LOG>)
{
# Good practice to store $_ value because
# subsequent operations may change it.
my($line) = $_;

# Good practice to always strip the trailing
# newline from the line.
chomp($line);

# Convert the line to upper case.
# $line =~ tr/[a-z]/[A-Z]/;

if($line =~ m/\$\(demonstration_problem(\d)\)\$/)
{
	# print "$line\n";
	print NewA "\tdemonstration_problem$1_step1.setImageName\(\"A_demonstration_problem$1.png\"\);\n";
	#print "demonstration_problem$1_step1.setImageName\(\"A_demonstration_problem$1.png\"\)\n";
	print NewB "\tdemonstration_problem$1_step1.setImageName\(\"B_demonstration_problem$1.png\"\);\n";
	#print "demonstration_problem$1_step1.setImageName\(\"B_demonstration_problem$1.png\"\)\n";
}

elsif($line =~ m/dorminLabel((\d)+)\.setImageName\(\"\$\((eq_problem(\d)(_var){0,1})\)\$\"\);/)
{
	#print "$line $1 $2 $3 $4\n";
	print NewA "\tdorminLabel$1.setImageName\(\"A_$3.png\"\);\n";
	print NewB "\tdorminLabel$1.setImageName\(\"B_$3.png\"\);\n";
}
elsif($line =~ m/dorminLabel((\d)+)\.setImageName\(\"\$\((LT_problem(\d)(_option(\d)){0,1})\)\$\"\);/)
{
	#print "$line $1 $2 $3 $4\n";
	print NewA "\tdorminLabel$1.setImageName\(\"A_$3.png\"\);\n";
	print NewB "\tdorminLabel$1.setImageName\(\"B_$3.png\"\);\n";
}
elsif($line =~ m/dorminLabel((\d)+)\.setImageName\(\"\$\((eqexp_problem(\d)(_option(\d)){0,1})\)\$\"\);/)
{
	#print "$line $1 $2 $3 $4\n";
	print NewA "\tdorminLabel$1.setImageName\(\"A_$3.png\"\);\n";
	print NewB "\tdorminLabel$1.setImageName\(\"B_$3.png\"\);\n";
}
elsif($line =~ m/dorminLabel((\d)+)\.setImageName\(\"\$\(effective_problem(\d)\)\$\"\);/)
{
	print NewA "\tdorminLabel$1.setImageName\(\"A_effective_problem$3.png\"\);\n";
	print NewB "\tdorminLabel$1.setImageName\(\"B_effective_problem$3.png\"\);\n";
}
elsif($line =~ m/(public\sclass\sTabbedPreTest\sextends\sjavax.swing.JPanel)/)
{
	print NewA "public class TabbedPreTestA extends javax.swing.JPanel{\n";
	print NewB "public class TabbedPreTestB extends javax.swing.JPanel{\n";
}
elsif($line =~ m/(public\sTabbedPreTest\(\)\s{)/)
{
	print NewA "public TabbedPreTestA() {\n";
	#print "$line\n";
	print NewB "public TabbedPreTestB() {\n";
	#print "$line\n";
}
elsif($line =~ m/(new\sCTAT_Launcher\(argv\).launch\s\(new\sTabbedPreTest\(\)\);)/)
{
	print NewA "new CTAT_Launcher(argv).launch (new TabbedPreTestA());\n";
	#print "$line\n";
	print NewB "new CTAT_Launcher(argv).launch (new TabbedPreTestB());";
	#print "$line\n";
}
else
{
	print NewA "$line\n";
	print NewB "$line\n";
}
}

close(newA);
close(newB);
close(LOG); 




