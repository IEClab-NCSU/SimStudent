use XML::Parser;
 
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

@BKEY_eq_solving = ("1/4","11/5","-9/7","4","-3/4","34/7");
@BKEY_LT_problem1 = (false,false,false,false,false,true,false);
@BKEY_LT_problem2 = (true,false,false,true,false,false,false);
@BKEY_LT_problem3 = (true,false,false,false,true,false,-1);
@BKEY_LT_problem4 = (true,true,false,false,true,false,-1);
@BKEY_LT_problem5 = (false,true,false,true,false,false,-1);
@BKEY_LT_problem6 = (false,false,true,false,false,true,-1);
@BKEY_effective_problem1 = (false,false,false,true);
@BKEY_effective_problem2 = (false,false,false,true);
@BKEY_effective_problem3 = (false,false,true,false);
@BKEY_eqexp_problem1 = (false,true,true,false,true);
@BKEY_eqexp_problem2 = (false,true,false,true,false);
@BKEY_Demonstration_problem = ("1","3","1","2","1");

##########################################
# Version A Key
###########################################

@AKEY_eq_solving = ("-1/4","7/5","-7/2","1","-4/3","13/6");
@AKEY_LT_problem1 = (false,false,false,false,false,true,false);
@AKEY_LT_problem2 = (false,true,true,false,false,false,false);
@AKEY_LT_problem3 = (true,false,false,false,true,false,-1);
@AKEY_LT_problem4 = (true,true,false,false,true,false,-1);
@AKEY_LT_problem5 = (false,false,true,false,false,true,-1);
@AKEY_LT_problem6 = (true,true,false,false,false,false,-1);
@AKEY_effective_problem1 = (false,false,false,true);
@AKEY_effective_problem2 = (false,false,false,true);
@AKEY_effective_problem3 = (false,false,true,false);
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

if($line =~ m/<property name=\"Selection\">/)
{
	$check_selection = 1;
	#print "$line\n";
}
elsif($check_selection == 1 && $line =~ m/(<entry>){1}(\w+)(<\/entry>){1}/)
{
	$check_selection = 0;
	$problem_var = $2;

	#print "Currently Grading: $problem_var\n";
	
	$problem_var =~ m/(LT|effective|eqexp|demonstration|eq|StudentID)/;

	$problem_type = $1;

	if($1 eq "eq")
	{
		$problem_var =~ m/(eq)_problem(\d)_box/;
		$grab_prob = $2;
		# print "get ready to grab answer for equation problem # $eq_prob\n";
	}
	elsif($1 eq "demonstration")
	{
		$problem_var =~ m/(demonstration)_problem(\d)(_){0,1}((.)?)/;
	
		# print "FOUND: $1 $2 $3 $4\n";
		if($3 eq "")
		{
			$problem_no = $2;
			# print "fooooo !#%^@$    PROBLEM IS DEMONSTRATION # $2\n";
			$grab_prob = 1;
		}
	}
	elsif ($1 ne "Done")
	{
		$problem_var =~ m/(LT|effective|eqexp|demonstration)_problem(\d)_option(\d)/;
		$problem_type = $1;
		$problem_no = $2;
		$problem_option = $3;
		$grab_prob = 1;
	}
	
	# print "FOUND: $1 $2 $3 $4\n";
}
elsif($grab_prob > 0)
{
	# print "$line\n";

	if($line =~ m/<property name=\"Input\">/)
	{
		#print "HAHAHAHA: #### $1\n";
		$grab_next_grab_prob = $grab_prob;
		$grab_prob = 0;
	}
	
}	
elsif($grab_next_grab_prob > 0)
{
	#print "$line\n";
	

	if($line =~ m/<entry>((\S|\s|\w|\*|-|\/|\.)+)<\/entry>/)
	{
		 #print "ANSWER IS HERE: $1\n";
		$problem_ans = lc($1);
		
	}


	#print "storing $problem_type $problem_no $problem_option $problem_ans\n";

	if($problem_type eq "StudentID")
		{
			$studentID = $problem_ans;
		}
	if($problem_type eq "eq")
	{
		#print "trying to get $grab_next_grab_prob : @eq_solving[$grab_next_grab_prob-1]\n";
		@eq_solving[$grab_next_grab_prob-1] = $problem_ans;
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
			$problem_ans =~ m/((\s)?)(\d)((\))(.)?){0,1}/;

			 #print "SAVING: DEMONSTRATION $problem_no with values 1:$1 2:$2 3:$3 4:$4 5:$5\n";

			$problem_ans = $3;			
			$Demonstration_problem[$problem_no-1] = $problem_ans;
		}

	$grab_next_grab_prob = 0;
}	

	 # print "$line\n";


}




print "Student ID: #$studentID \n";

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

if(shift @ARGV eq "B")
{
	GradeTestB();
}
else
{
	GradeTestA();
}

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

close(LOG); 







#########################################
# Grading Functions
#########################################

sub GradeTestB 
{
   print "\n\n\n*****  Begin Grading (Version B)  *****\n";

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
print "\n"; 

print "\n\n*****  Grading Done (Version B)  *****\n";
}





sub GradeTestA 
{
   print "\n\n\n*****  Begin Grading (Version A)  *****\n";

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
print "\n"; 

print "\n\n*****  Grading Done (Version A)  *****\n";
}