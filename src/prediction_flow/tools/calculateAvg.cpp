#include "iostream"
#include "string"
#include "vector"
#include "cstdlib"
typedef std::vector<std::string> strVectorType;

strVectorType split(std::string str)
{
	strVectorType ret ;
	while( str != "" )
	{
		std::string::size_type pos = str.find("\t") ;
		std::string item ;
		if( pos == std::string::npos )
		{
			item = str ;
			str = "" ;
		}
		else
		{
			item = str.substr(0,pos) ;
			str = str.substr(pos+1) ;
		}
		ret.push_back(item) ;
	}
	return ret ;
}


int main()
{
	std::string line ;
	int counts = 0 ;
	int days = 0 ;
	std::string ansPdps="" ;
	while( std::getline(std::cin,line) )
	{
		strVectorType strArray = split(line) ;
		if( strArray.size() != 3 )
			continue ;
		std::string nowPdps = strArray[0] ;
		if( nowPdps != ansPdps )
		{
			if( ansPdps == "" )
			{
				ansPdps = nowPdps ;
				counts = atoi(strArray[1].c_str()) ;
				days = 1 ;
			}
			else
			{
				std::cout << ansPdps << "\t" << (counts/days) << std::endl ;
				ansPdps = nowPdps ;
				counts = atoi(strArray[1].c_str()) ;
				days = 1 ;
			}
		}
		else
		{
			++ days ;
			counts += atoi(strArray[1].c_str()) ;
			
		}
	}
	if( days > 0 )
	{
		std::cout << ansPdps << "\t" << (counts/days) << std::endl ;
	}
	return 0 ;
}
