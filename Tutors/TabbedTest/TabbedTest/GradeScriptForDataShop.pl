use XML::Parser;
 

@Scores_eq = ();
@Scores_LT = ();
@Scores_effective = ();
@Scores_eqexp = ();
@Scores_demonstration = ();


$studentID = -1;
$anon_Student_ID = -1;
$session_ID = -1;
$currentVersion = -1;
$PrePost = -1;
$action = -1;
@eq_solving = qw(-1,-1,-1,-1,-1,-1);
@LT_problem1 = (-1,-1,-1,-1,-1,-1,-1);
@LT_problem2 = (-1,-1,-1,-1,-1,-1,-1);
@LT_problem3 = (-1,-1,-1,-1,-1,-1,-1);
@LT_problem4 = (-1,-1,-1,-1,-1,-1,-1);
@LT_problem5 = (-1,-1,-1,-1,-1,-1,-1);
@LT_problem6 = (-1,-1,-1,-1,-1,-1,-1);
@effective_problem1 = (-1,-1,-1,-1);
@effective_problem2 = (-1,-1,-1,-1);
@effective_problem3 = (-1,-1,-1,-1);
@eqexp_problem1 = (-1,-1,-1,-1,-1);
@eqexp_problem2 = (-1,-1,-1,-1,-1);
@Demonstration_problem = (-1,-1,-1,-1,-1);

###########################################
# Version B Key
###########################################

# @BKEY_eq_solving = ("1/4","11/5","-9/7","4","-3/4","34/7");
@BKEY_eq_solving = ("3","3","-3","4","-3","-2.8");
@BKEY_LT_problem1 = (false,false,false,false,false,true,false);
@BKEY_LT_problem2 = (true,false,false,true,false,false,false);
@BKEY_LT_problem3 = (true,false,false,false,true,false,-1);
@BKEY_LT_problem4 = (true,true,false,false,true,false,-1);
@BKEY_LT_problem5 = (false,true,false,true,false,false,-1);
@BKEY_LT_problem6 = (false,false,true,false,false,true,-1);
@BKEY_effective_problem1 = (false,false,false,true);
@BKEY_effective_problem2 = (false,false,false,true);
@BKEY_effective_problem3 = (false,false,false,true);
@BKEY_eqexp_problem1 = (false,true,true,false,true);
@BKEY_eqexp_problem2 = (false,true,false,true,false);
@BKEY_Demonstration_problem = ("1","3","1","2","1");

###########################################
# Version A Key
###########################################

# @AKEY_eq_solving = ("-1/4","7/5","-7/2","1","-4/3","13/6");
@AKEY_eq_solving = ("-2","3","-2","3","-2","7.2");
@AKEY_LT_problem1 = (false,false,false,false,false,true,false);
@AKEY_LT_problem2 = (false,true,true,false,false,false,false);
@AKEY_LT_problem3 = (true,false,false,false,true,false,-1);
@AKEY_LT_problem4 = (true,true,false,false,true,false,-1);
@AKEY_LT_problem5 = (false,false,true,false,false,true,-1);
@AKEY_LT_problem6 = (true,true,false,false,false,false,-1);
@AKEY_effective_problem1 = (false,false,false,true);
@AKEY_effective_problem2 = (false,false,false,true);
@AKEY_effective_problem3 = (false,false,false,true);
@AKEY_eqexp_problem1 = (true,false,false,false,true);
@AKEY_eqexp_problem2 = (true,false,true,false,true);
@AKEY_Demonstration_problem = ("1","3","1","2","1");

# initialize parser and read the file
#
# so i guess we won't be using the following XML parser
# $parser = new XML::Parser( Style => 'Tree' );
# my $tree = $parser->parsefile( shift @ARGV );
# 
# serialize the structure
# use Data::Dumper;
# print Dumper( $tree );
#
#

open (LOG, shift @ARGV) || die "AWWWWWWWWWWWWWWWW!"; 
open (SCORES, "> scores_sheet.txt") || die "AWWWWWWWWWWWWWWWW!";



print SCORES "AnonStudentID\t";
print SCORES "SessionID\t";
#print SCORES "StudentID\t";
print SCORES "Test\t";
print SCORES "Version\t";
print SCORES "eq\t";
print SCORES "eq average\t";
print SCORES "LT\t";
print SCORES "LT average\t";
print SCORES "effective\t";
print SCORES "effective average\t";
print SCORES "eqexp\t";
print SCORES "eqexp average\t";
print SCORES "demonstration\t";
print SCORES "demonstration average\t";
print SCORES "Overall verage\n";


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

if($line =~ m/(([.]|[^\t])+)\t(([.]|[^\t])+)\t(([.]|[^\t])+)\t(([.]|[^\t])+)\t(([.]|[^\t])+)\t(([.]|[^\t])+)\t(([.]|[^\t])+)\t\t(([.]|[^\t])+)\t\t(([.]|[^\t])+)\t(([.]|[^\t])+)\t(([.]|[^\t])+)\t(([.]|[^\t])+)\t(([.]|[^\t])+)\t(([.]|[^\t])+)\t(([.]|[^\t])+)\t(([.]|[^\t])+)/)
{
	#print "$line\n";
	#print "$1 $3 $5 $7 $9 $11 $13 $15 $17 $19 $21 $23 $25 $27 $29 $31 $33 $35 $37 $39 $41\n";
	#print "$3 $5 $27 $29\n";
	$newSession_ID = $5;
	$anon_Student_ID = $3;
	$problem_var = $27;
	$problem_ans = lc($31);
	$tempVersion = $17;
	$action = $29;
}


	$problem_var =~ m/(LT|effective|eqexp|demonstration|StudentID|eq)(_problem(\d)(_option(\d)){0,1}){0,1}/;
	$problem_type = $1;
	$problem_no = $3;
	$problem_option = $5;
	#print "$1 $3 $4 $5\n";
	#print "$problem_var\n";
	#print SCORES "$1\n";
	

	if($session_ID ne $newSession_ID)
	{

		if($session_ID ne "-1" && $session_ID ne "")
		{
			#print "@Demonstration_problem\n";	
			#ShowLog();
				
			if($currentVersion eq "B")
			{
				GradeTestB();
			}
			else
			{
				GradeTestA();
			}
	
			CalcTotal();

			@eq_solving = qw(-1,-1,-1,-1,-1,-1);
			@LT_problem1 = (-1,-1,-1,-1,-1,-1,-1);
			@LT_problem2 = (-1,-1,-1,-1,-1,-1,-1);
			@LT_problem3 = (-1,-1,-1,-1,-1,-1,-1);
			@LT_problem4 = (-1,-1,-1,-1,-1,-1,-1);
			@LT_problem5 = (-1,-1,-1,-1,-1,-1,-1);
			@LT_problem6 = (-1,-1,-1,-1,-1,-1,-1);
			@effective_problem1 = (-1,-1,-1,-1);
			@effective_problem2 = (-1,-1,-1,-1);
			@effective_problem3 = (-1,-1,-1,-1);
			@eqexp_problem1 = (-1,-1,-1,-1,-1);
			@eqexp_problem2 = (-1,-1,-1,-1,-1);
			@Demonstration_problem = (-1,-1,-1,-1,-1);

		}
		$session_ID = $newSession_ID;

		if($tempVersion =~ m/(Pre|Post)-Test Version (A|B)/)
		{
			#print "$line\n\n";
			#print "$currentVersion $session_ID : $1 $2 $3\n\n";		
			$currentVersion = $2;
			$PrePost = $1;
		}

	}


	if($problem_type eq "StudentID")
	{
		$studentID = $problem_ans;
	}
	elsif($problem_type eq "eq")
	{
		@eq_solving[$problem_no-1] = $problem_ans;
		#print "$problem_no\n";
	}
	elsif($problem_type eq "LT")
		{
			
			if($problem_no eq "1")
			{
				$LT_problem1[$problem_option-1] = $problem_ans;
			}
			elsif($problem_no eq "2")
			{
				$LT_problem2[$problem_option-1] = $problem_ans;
			}
			elsif($problem_no eq "3")	
			{
				$LT_problem3[$problem_option-1] = $problem_ans;
			}
			elsif($problem_no eq "4")
			{
				$LT_problem4[$problem_option-1] = $problem_ans;
			}
			elsif($problem_no eq "5")
			{
				$LT_problem5[$problem_option-1] = $problem_ans;
			}
			elsif($problem_no eq "6")
			{
				$LT_problem6[$problem_option-1] = $problem_ans;
			}
		}
		elsif($problem_type eq "effective")
		{
			
			if($problem_no eq "1")
			{
				$effective_problem1[$problem_option-1] = $problem_ans;
				
				if($effective_problem1[$problem_option-1] eq "agree")
				{
					$effective_problem1[$problem_option-1] = true;
				}
				elsif($effective_problem1[$problem_option-1] eq "disagree")
				{
					$effective_problem1[$problem_option-1] = false;
				}
			}
			elsif($problem_no eq "2")
			{
				$effective_problem2[$problem_option-1] = $problem_ans;

				if($effective_problem2[$problem_option-1] eq "agree")
				{
					$effective_problem2[$problem_option-1] = true;
				}
				elsif($effective_problem2[$problem_option-1] eq "disagree")
				{
					$effective_problem2[$problem_option-1] = false;
				}
			}
			elsif($problem_no eq "3")
			{
				$effective_problem3[$problem_option-1] = $problem_ans;

				if($effective_problem3[$problem_option-1] eq "agree")
				{
					$effective_problem3[$problem_option-1] = true;
				}
				elsif($effective_problem3[$problem_option-1] eq "disagree")
				{
					$effective_problem3[$problem_option-1] = false;
				}
			}
		}
		elsif($problem_type eq "eqexp")
		{
			if($problem_no eq "1")
			{
				$eqexp_problem1[$problem_option-1] = $problem_ans;
			}
			elsif($problem_no eq "2")
			{
				$eqexp_problem2[$problem_option-1] = $problem_ans;
			}
			elsif($problem_no eq "3")
			{
				$eqexp_problem3[$problem_option-1] = $problem_ans;
			}
		}
		elsif($problem_type eq "demonstration" && $problem_no ne "")
		{
			#print "$problem_ans\n";
			$problem_ans =~ m/^(\d)(\)){0,1}/;
			$first_match = $1;

			if ($1 =~ /^-?\d/ && $action eq "UpdateTextField")
			{
				#print "dddd: $action\n";
				#print "SAVING: DEMONSTRATION $problem_no with values 1:$first_match 2:$2 3:$3 4:$4 5:$5\n";
				$problem_ans = $first_match;
				$Demonstration_problem[$problem_no-1] = $problem_ans;
			}

		}









}


close (LOG);
close (SCORES);























sub ShowLog()
{
print "===============================================\n";
print "Session ID: #$session_ID \n";

print "EQ_Solving: ";

for(my $i = 0; $i < 6; $i++)
{
	print $eq_solving[$i];
	print "\t\t";
}
print "\n";

print "LT_Problem1: ";

for(my $i = 0; $i < 7; $i++)
{
	print $LT_problem1[$i];
	print " ";
}
print "\n";

print "LT_Problem2: ";

for(my $i = 0; $i < 7; $i++)
{
	print $LT_problem2[$i];
	print " ";
}
print "\n";

print "LT_Problem3: ";

for(my $i = 0; $i < 7; $i++)
{
	print $LT_problem3[$i];
	print " ";
}
print "\n";

print "LT_Problem4: ";

for(my $i = 0; $i < 7; $i++)
{
	print $LT_problem4[$i];
	print " ";
}
print "\n";

print "LT_Problem5: ";

for(my $i = 0; $i < 7; $i++)
{
	print $LT_problem5[$i];
	print " ";
}
print "\n";

print "LT_Problem6: ";

for(my $i = 0; $i < 7; $i++)
{
	print $LT_problem6[$i];
	print " ";
}
print "\n";

print "Effective_Problem1: ";

for(my $i = 0; $i < 4; $i++)
{
	print $effective_problem1[$i];
	print " ";
}
print "\n";


print "Effective_Problem2: ";

for(my $i = 0; $i < 4; $i++)
{
	print $effective_problem2[$i];
	print " ";
}
print "\n";


print "Effective_Problem3: ";

for(my $i = 0; $i < 4; $i++)
{
	print $effective_problem3[$i];
	print " ";
}
print "\n";


print "EqExp_Problem1: ";

for(my $i = 0; $i < 5; $i++)
{
	print $eqexp_problem1[$i];
	print " ";
}
print "\n";


print "EqExp_Problem2: ";

for(my $i = 0; $i < 5; $i++)
{
	print $eqexp_problem2[$i];
	print " ";
}
print "\n"; 

print "Demonstration_Problem: ";

for(my $i = 0; $i < 5; $i++)
{
	print $Demonstration_problem[$i];
	print " ";
}
print "\n"; 
print "===============================================\n";
print "\n"; 
}









sub GradeTestB 
{
   print "*****  Begin Grading (Version B)  *****\n";
   print "Session ID: #$session_ID \n";
   print "EQ_Solving: ";

for(my $i = 0; $i < 6; $i++)
{
	# print "matching: @eq_solving[$i] and @BKEY_eq_solving[$i], they are the same: (@BKEY_eq_solving[$i] = @eq_solving[$i])\n";
	@eq_solving[$i] = (@eq_solving[$i] eq @BKEY_eq_solving[$i]) ? "true": "false";
	print @eq_solving[$i];
	print " ";
}
print "\n";

print "LT_Problem1: ";

for(my $i = 0; $i < 7; $i++)
{
	@LT_problem1[$i] = @LT_problem1[$i] eq @BKEY_LT_problem1[$i] ? "true": "false";
	print @LT_problem1[$i];
	print " ";
}
print "\n";

print "LT_Problem2: ";

for(my $i = 0; $i < 7; $i++)
{
	$LT_problem2[$i] = $LT_problem2[$i] eq $BKEY_LT_problem2[$i] ? "true": "false";
	print $LT_problem2[$i];
	print " ";
}
print "\n";

print "LT_Problem3: ";

for(my $i = 0; $i < 6; $i++)
{
	$LT_problem3[$i] = $LT_problem3[$i] eq $BKEY_LT_problem3[$i] ? "true": "false";
	print $LT_problem3[$i];
	print " ";
}
print "\n";

print "LT_Problem4: ";

for(my $i = 0; $i < 6; $i++)
{
	$LT_problem4[$i] = $LT_problem4[$i] eq $BKEY_LT_problem4[$i] ? "true": "false";
	print $LT_problem4[$i];
	print " ";
}
print "\n";

print "LT_Problem5: ";

for(my $i = 0; $i < 6; $i++)
{
	$LT_problem5[$i] = $LT_problem5[$i] eq $BKEY_LT_problem5[$i] ? "true": "false";
	print $LT_problem5[$i];
	print " ";
}
print "\n";

print "LT_Problem6: ";

for(my $i = 0; $i < 6; $i++)
{
	$LT_problem6[$i] = $LT_problem6[$i] eq $BKEY_LT_problem6[$i] ? "true": "false";
	print $LT_problem6[$i];
	print " ";
}
print "\n";

print "Effective_Problem1: ";

for(my $i = 0; $i < 4; $i++)
{
	$effective_problem1[$i] = $effective_problem1[$i] eq $BKEY_effective_problem1[$i] ? "true": "false";
	print $effective_problem1[$i];
	print " ";
}
print "\n";


print "Effective_Problem2: ";

for(my $i = 0; $i < 4; $i++)
{
	$effective_problem2[$i] = $effective_problem2[$i] eq $BKEY_effective_problem2[$i] ? "true": "false";
	print $effective_problem2[$i];
	print " ";
}
print "\n";


print "Effective_Problem3: ";

for(my $i = 0; $i < 4; $i++)
{
	$effective_problem3[$i] = $effective_problem3[$i] eq $BKEY_effective_problem3[$i] ? "true": "false";
	print $effective_problem3[$i];
	print " ";
}
print "\n";


print "EqExp_Problem1: ";

for(my $i = 0; $i < 5; $i++)
{
	$eqexp_problem1[$i] = $eqexp_problem1[$i] eq $BKEY_eqexp_problem1[$i] ? "true": "false";
	print $eqexp_problem1[$i];
	print " ";
}
print "\n";


print "EqExp_Problem2: ";

for(my $i = 0; $i < 5; $i++)
{
	$eqexp_problem2[$i] = $eqexp_problem2[$i] eq $BKEY_eqexp_problem2[$i] ? "true": "false";
	print $eqexp_problem2[$i];
	print " ";
}
print "\n"; 

print "Demonstration_Problem: ";

for(my $i = 0; $i < 5; $i++)
{
	$Demonstration_problem[$i] = $Demonstration_problem[$i] eq $BKEY_Demonstration_problem[$i] ? "true": "false";
	print $Demonstration_problem[$i];
	print " ";
}
print "\n*****  Grading Done (Version B)  *****\n";
}





sub GradeTestA 
{
   print "*****  Begin Grading (Version A)  *****\n";
   print "Session ID: #$session_ID \n";
   print "EQ_Solving: ";

for(my $i = 0; $i < 6; $i++)
{
	# print "matching: @eq_solving[$i] and @AKEY_eq_solving[$i], they are the same: (@AKEY_eq_solving[$i] = @eq_solving[$i])\n";
	@eq_solving[$i] = (@eq_solving[$i] eq @AKEY_eq_solving[$i]) ? "true": "false";
	print @eq_solving[$i];
	print " ";
}
print "\n";

print "LT_Problem1: ";

for(my $i = 0; $i < 7; $i++)
{
	@LT_problem1[$i] = @LT_problem1[$i] eq @AKEY_LT_problem1[$i] ? "true": "false";
	print @LT_problem1[$i];
	print " ";
}
print "\n";

print "LT_Problem2: ";

for(my $i = 0; $i < 7; $i++)
{
	$LT_problem2[$i] = $LT_problem2[$i] eq $AKEY_LT_problem2[$i] ? "true": "false";
	print $LT_problem2[$i];
	print " ";
}
print "\n";

print "LT_Problem3: ";

for(my $i = 0; $i < 6; $i++)
{
	$LT_problem3[$i] = $LT_problem3[$i] eq $AKEY_LT_problem3[$i] ? "true": "false";
	print $LT_problem3[$i];
	print " ";
}
print "\n";

print "LT_Problem4: ";

for(my $i = 0; $i < 6; $i++)
{
	$LT_problem4[$i] = $LT_problem4[$i] eq $AKEY_LT_problem4[$i] ? "true": "false";
	print $LT_problem4[$i];
	print " ";
}
print "\n";

print "LT_Problem5: ";

for(my $i = 0; $i < 6; $i++)
{
	$LT_problem5[$i] = $LT_problem5[$i] eq $AKEY_LT_problem5[$i] ? "true": "false";
	print $LT_problem5[$i];
	print " ";
}
print "\n";

print "LT_Problem6: ";

for(my $i = 0; $i < 6; $i++)
{
	$LT_problem6[$i] = $LT_problem6[$i] eq $AKEY_LT_problem6[$i] ? "true": "false";
	print $LT_problem6[$i];
	print " ";
}
print "\n";

print "Effective_Problem1: ";

for(my $i = 0; $i < 4; $i++)
{
	$effective_problem1[$i] = $effective_problem1[$i] eq $AKEY_effective_problem1[$i] ? "true": "false";
	print $effective_problem1[$i];
	print " ";
}
print "\n";


print "Effective_Problem2: ";

for(my $i = 0; $i < 4; $i++)
{
	$effective_problem2[$i] = $effective_problem2[$i] eq $AKEY_effective_problem2[$i] ? "true": "false";
	print $effective_problem2[$i];
	print " ";
}
print "\n";


print "Effective_Problem3: ";

for(my $i = 0; $i < 4; $i++)
{
	$effective_problem3[$i] = $effective_problem3[$i] eq $AKEY_effective_problem3[$i] ? "true": "false";
	print $effective_problem3[$i];
	print " ";
}
print "\n";


print "EqExp_Problem1: ";

for(my $i = 0; $i < 5; $i++)
{
	$eqexp_problem1[$i] = $eqexp_problem1[$i] eq $AKEY_eqexp_problem1[$i] ? "true": "false";
	print $eqexp_problem1[$i];
	print " ";
}
print "\n";


print "EqExp_Problem2: ";

for(my $i = 0; $i < 5; $i++)
{
	$eqexp_problem2[$i] = $eqexp_problem2[$i] eq $AKEY_eqexp_problem2[$i] ? "true": "false";
	print $eqexp_problem2[$i];
	print " ";
}
print "\n"; 

print "Demonstration_Problem: ";

for(my $i = 0; $i < 5; $i++)
{
	$Demonstration_problem[$i] = $Demonstration_problem[$i] eq $BKEY_Demonstration_problem[$i] ? "true": "false";
	print $Demonstration_problem[$i];
	print " ";
}

print "\n*****  Grading Done (Version A)  *****\n";
}










sub CalcTotal()
{
$num_total = 0;
$num_valid_items = 0;
$Eq_problems = 0;
$LT_problems = 0;
$Effective_problems = 0;
$EqExp_problems = 0;
$Demonstration_problems = 0;

for(my $i = 0; $i < 6; $i++)
{
	if($eq_solving[$i] eq "true")
	{
		$num_total++;
		$Eq_problems++;
	}
	$num_valid_items++;
}

for(my $i = 0; $i < 7; $i++)
{
	if($LT_problem1[$i] eq "true")
	{
		$num_total++;
		$LT_problems++;
	}
	$num_valid_items++;
}

for(my $i = 0; $i < 7; $i++)
{
	if($LT_problem2[$i] eq "true")
	{
		$num_total++;
		$LT_problems++;
	}
	$num_valid_items++;
}


for(my $i = 0; $i < 6; $i++)
{
	if($LT_problem3[$i] eq "true")
	{
		$num_total++;
		$LT_problems++;
	}
	$num_valid_items++;
}

for(my $i = 0; $i < 6; $i++)
{
	if($LT_problem4[$i] eq "true")
	{
		$num_total++;
		$LT_problems++;
	}
	$num_valid_items++;
}

for(my $i = 0; $i < 6; $i++)
{
	if($LT_problem5[$i] eq "true")
	{
		$num_total++;
		$LT_problems++;
	}
	$num_valid_items++;
}

for(my $i = 0; $i < 6; $i++)
{
	if($LT_problem6[$i] eq "true")
	{
		$num_total++;
		$LT_problems++;
	}
	$num_valid_items++;
}

for(my $i = 0; $i < 4; $i++)
{
	if($effective_problem1[$i] eq "true")
	{
		$num_total++;
		$Effective_problems++;
	}
	$num_valid_items++;
}

for(my $i = 0; $i < 4; $i++)
{
	if($effective_problem2[$i] eq "true")
	{
		$num_total++;
		$Effective_problems++;
	}
	$num_valid_items++;
}

for(my $i = 0; $i < 4; $i++)
{
	if($effective_problem3[$i] eq "true")
	{
		$num_total++;
		$Effective_problems++;
	}
	$num_valid_items++;
}

for(my $i = 0; $i < 5; $i++)
{
	if($eqexp_problem1[$i] eq "true")
	{
		$num_total++;
		$EqExp_problems++;
	}
	$num_valid_items++;
}

for(my $i = 0; $i < 5; $i++)
{
	if($eqexp_problem2[$i] eq "true")
	{
		$num_total++;
		$EqExp_problems++;
	}
	$num_valid_items++;
}

for(my $i = 0; $i < 5; $i++)
{
	if($Demonstration_problem[$i] eq "true")
	{
		$num_total++;
		$Demonstration_problems++;
	}
	$num_valid_items++;
}

$preTestAverage = $num_total / $num_valid_items;
print "Total Items: $num_valid_items.\n"; 
print "Correct Items: $num_total.\n";
print "\tPre-Test Score Average: $preTestAverage.\n"; 
if($preTestAverage > 0.7602)
	{
		print "\tThis participant is above the 95th percentile!\n";
	}
elsif($preTestAverage < 0.1634)
	{
		print "\tThis participant is below the 5th percentile!\n";
	}

print "Correct Eq Solving Items: $Eq_problems.\n"; 
print "Corrct Like Term Items: $LT_problems.\n"; 
print "Correct Effective Step Items: $Effective_problems.\n"; 
print "Correct EQ Items: $EqExp_problems.\n"; 
print "Correct Demonstration Items: $Demonstration_problems.\n"; 

print "*****  Score Calculated  *****\n";


print SCORES "$anon_Student_ID\t";
print SCORES "$session_ID\t";
#print SCORES "$studentID\t";
print SCORES "$PrePost\t";
print SCORES "$currentVersion\t";
print SCORES "$Eq_problems\t";
	$Eq_average = ($Eq_problems/6);
print SCORES "$Eq_average\t";
print SCORES "$LT_problems\t";
	$LT_average = ($LT_problems/38);
print SCORES "$LT_average\t";
print SCORES "$Effective_problems\t";
	$Effective_average = ($Effective_problems/12);
print SCORES "$Effective_average\t";
print SCORES "$EqExp_problems\t";
	$EqExp_average = ($EqExp_problems/10);
print SCORES "$EqExp_average\t";
print SCORES "$Demonstration_problems\t";
	$Demonstration_average = ($Demonstration_problems/5);
print SCORES "$Demonstration_average\t";
print SCORES "$preTestAverage\n";



@eq_solving = qw(-1,-1,-1,-1,-1,-1);
@LT_problem1 = (-1,-1,-1,-1,-1,-1,-1);
@LT_problem2 = (-1,-1,-1,-1,-1,-1,-1);
@LT_problem3 = (-1,-1,-1,-1,-1,-1,-1);
@LT_problem4 = (-1,-1,-1,-1,-1,-1,-1);
@LT_problem5 = (-1,-1,-1,-1,-1,-1,-1);
@LT_problem6 = (-1,-1,-1,-1,-1,-1,-1);
@effective_problem1 = (-1,-1,-1,-1);
@effective_problem2 = (-1,-1,-1,-1);
@effective_problem3 = (-1,-1,-1,-1);
@eqexp_problem1 = (-1,-1,-1,-1,-1);
@eqexp_problem2 = (-1,-1,-1,-1,-1);
@Demonstration_problem = (-1,-1,-1,-1,-1);


}