#!/bin/bash
if !command -v "java -version" &>/dev/null; then #just to check if java is installed or not
  echo "Java is not installed. Please install Java".
  exit
fi

if !command -v "ant -version" &>/dev/null; then #just to check ant is installed or not.
  echo "Ant is not installed. Please install Ant".
  exit
fi

name="distribution"
verbose="N"
function show_usage() {
  printf "Usage: ./createDistribution.sh [options [parameters]]\n"
  printf "\nThis shell script generates distribution for simStudent.\n"
  printf "Options:\n"
  printf " -n|--name [name], provide distribution name. Default: distribution\n"
  printf " -v|--verbose, verbose ant logs printed on terminal\n"
  printf " -h|--help, prints shell script options\n"
  exit 0
}
case $1 in
--name | -n)
  shift
  name="$1"
  shift
  ;;
--verbose | -v)
  shift
  verbose="Y"
  ;;
--help | -h)
  shift
  show_usage
  ;;
*)
  ;;
esac
printf "\nUsing java version: "
command java -version
printf "\nUsing ant version: "
command ant -version

printf "\nDistribution name - $name"
cd "../APLUS/java/"

printf "\nCreating ctat.jar..."
if [[ "$verbose" = "Y" ]]; then
  ant clean #remove previous ctat.jar
  ant  #Create new ctat.jar
else
  ant clean 1>/dev/null 2>&1
  ant 1>/dev/null 2>&1
fi

cd "../../Tutors/Algebra/SimStAlgebraV8"
printf "\nCompiling tutors..."

if [[ "$verbose" = "Y" ]]; then
  command "./compileTutor.sh"
else
  command "./compileTutor.sh" 1>/dev/null 2>&1
fi

cd "../../"

if [ -d "./$name" ]; then
  printf "\nDistribution name $name already exists and will be deleted to create new distribution.\n"
  rm -rf "$name"
fi

mkdir "$name"
printf "\nCopying content of ./Algebra/ folder to ./$name/ folder..."
cp -a "./Algebra/." "$name/"

printf "\nCopying jars..."
rm "./$name/lib/ctat.jar"
cp "../APLUS/java/lib/ctat.jar" "./$name/lib/"
cp "../APLUS/java/lib/jsoup-1.15.3.jar" "./$name/lib/"
cp -a "../SIDE&SLIM jars/." "./$name/lib/"

printf "\nCompiling lightside files..."
if [[ "$verbose" = "Y" ]]; then
  command "ant clean"
  command "ant"
else
  command "ant clean" 1>/dev/null 2>&1
  command "ant" 1>/dev/null 2>&1
fi


printf "\nCopying lightside files..."
cp -a "../Lightside/lightside" "./$name/"


printf "\nDistribution - '$name' created successfully.\n"
