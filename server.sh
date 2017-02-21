#!/bin/sh

# Initialize our own variables:
DATABASE_IP="localhost:3306"
PORT="4445"
UNZIP_DIRECTORY="~/IEDCS_temp"
ENCFS_STORE="~/IEDCS_encfs-store"
ENCFS_MOUNT="~/IEDCS_encfs-mount"

trap "cd & fusermount -u ~/IEDCS_encfs-mount > /dev/null" 2
for i do
    case "$i" in
		-p|--port) PORT=$2;shift;;
		-d|--database) DATABASE_IP=$2;shift;;
		-u|--unzip) UNZIP_DIRECTORY=$2;shift;;
		-h|\?|--help)
			echo "Usage: ./script.sh [options]"
			echo "Default: localhost:4445; Database:localhost::3306"
			echo "	-d,--database <arg>   Database IP:PORT"
			echo "	-h,--help             Prints this help"
			echo "	-p,--port <arg>       Listening port"
			echo "-u unzip_directory"
			exit 0
		;;
    esac
done

echo $PORT
echo $DATABASE_IP

mkdir UNZIP_DIRECTORY > /dev/null
unzip server_pack.zip -d UNZIP_DIRECTORY > /dev/null
if [ $? -eq 0 ]; then
	echo "1) Unzip successfull"
else
	echo "1) Unzip error: $?"
	exit 1
fi

mkdir -p ENCFS_STORE > /dev/null
if [ $? -eq 0 ]; then
	echo "2) Successfully created ~/IEDCS_encfs-store"
else
	echo "2) Error: $?"
	exit 2
fi


mkdir -p ENCFS_MOUNT > /dev/null
if [ $? -eq 0 ]; then
	echo "3) Successfully created ~/IEDCS_encfs-mount"
else
	echo "3) Error: $?"
	exit 3
fi

echo "encfs $ENCFS_STORE $ENCFS_MOUNT"
encfs ~/IEDCS_encfs-store ~/IEDCS_encfs-mount
if [ $? -eq 0 ]; then
	echo "4) Successfully mounted ~/IEDCS_encfs-store on ~/IEDCS_encfs-mount"
else
	echo "4) Please try to use command 'fusermount -u ~/IEDCS_encfs-mount'"
	echo "4) Error: $?"
	exit 4
fi


cp -r UNZIP_DIRECTORY/CC_KS UNZIP_DIRECTORY/CitizenCard.cfg UNZIP_DIRECTORY/ebooks UNZIP_DIRECTORY/lib UNZIP_DIRECTORY/Makefile UNZIP_DIRECTORY/mySrvKeystore ~/IEDCS_encfs-mount > /dev/null
if [ $? -eq 0 ]; then
	echo "5) Successfully stored all components on ~/IEDCS_encfs-mount"
else
	echo "5) Error: $?"
	exit 5
fi

cp UNZIP_DIRECTORY/server.jar .
if [ $? -eq 0 ]; then
	echo "6) Successfully copied server.jar to current directory."
else
	echo "6) Error: $?"
	exit 6
fi

rm -rf UNZIP_DIRECTORY > /dev/null
if [ $? -eq 0 ]; then
	echo "7) Successfully removed temporary files"
else
	echo "7) Error: $?"
	exit 7
fi

chroot ~/IEDCS_encfs-mount /usr/bin/java -jar server.jar > /dev/null
if [ $? -ne 0 ]; then
	cp server.jar ~/IEDCS_encfs-mount
	java -jar ~/IEDCS_encfs-mount/server.jar -p $PORT -d $DATABASE_IP
fi

cd
fusermount -u ~/IEDCS_encfs-mount
if [ $? -eq 0 ]; then
	echo "Directory unmounted"
	echo "Service will shutdown"
	exit 0
else
	echo "Error unmounting"
	exit 10
fi