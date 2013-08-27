#ifndef _INIT_
#define _INIT_

#include <ctime>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <boost/asio.hpp>
#include <boost/bind.hpp>
#include <boost/thread.hpp>

#include "../include/battleship/Battleship.h"
#include "../include/battleship/Battleship_Define.h"

#include "../include/websocketpp/roles/client.hpp"
#include "../include/websocketpp/websocketpp.hpp"

#include "../include/battleship/AppClientHandler.hpp"
#include "../include/battleship/AppClientHandler_Define.h"
#include "ArtificialIntelligence.h"
#include "constant.h"

using namespace std;
using namespace websocketapp;
using boost::asio::ip::tcp;


AppClientHandlerPtr		s_con_handler;
std::string				s_uri;

//AI declare
Battleship*				p_Battleship; 
int		bot_table_x;
int		bot_table_y;
char*	bot_name;
char*	bot_avatar;

void Connectiont()
{
	std::cout << "Init connection..." << std::endl;
	s_uri = "ws://localhost:1338/";
	try {        
        client::connection_ptr con;
        client endpoint(s_con_handler);
        
        endpoint.alog().unset_level(websocketpp::log::alevel::ALL);
        endpoint.elog().unset_level(websocketpp::log::elevel::ALL);
        
        endpoint.elog().set_level(websocketpp::log::elevel::RERROR);
        endpoint.elog().set_level(websocketpp::log::elevel::FATAL);
        
        con = endpoint.get_connection(s_uri);
        
        con->add_request_header("User-Agent","WebSocket++/0.2.0 WebSocket++Chat/0.2.0");
        con->add_subprotocol("com.zaphoyd.websocketpp.chat");
        
        con->set_origin("http://localhost");

        endpoint.connect(con);
		boost::thread thr_conection(boost::bind(&client::run, &endpoint, false));
		thr_conection.join();
        
    } catch (std::exception& e) {
        std::cerr << "Exception: " << e.what() << std::endl;
    }	
};

void Get_BOT_Name(int argc, char* argv[]){

	if(argc==5)
	{
		bot_table_x = atoi(argv[1]);
		bot_table_y = atoi(argv[2]);
		bot_name = argv[3];
		bot_avatar = argv[4];
	}
	else
	{
		bot_table_x = 1;
		bot_table_y = 1;
		bot_name = "NO_NAME";
		bot_avatar ="NULL";
	}
};

#endif