#include "init.h"

//================================//
//	---		for User	 ---
//--------------------------------//
int my_map_1[][5] = {{2,0,0,0},{3,0,2,0},{3,0,4,0},{4,0,6,0},{5,0,9,0}}; //Isral map
int my_map_2[][5] = {{2,8,0,0},{3,0,1,0},{3,9,7,1},{4,6,2,0},{5,0,3,0}};
int my_map_3[][5] = {{2,7,6,0},{3,4,1,0},{3,2,6,1},{4,5,9,0},{5,3,3,0}};
int my_map_4[][5] = {{2,1,0,0},{3,7,1,1},{3,1,7,0},{4,0,2,1},{5,4,9,0}};
int my_map_5[][5] = {{2,7,4,0},{3,1,4,1},{3,6,9,0},{4,4,2,0},{5,4,5,1}};

ArtificialIntelligence *ai = new ArtificialIntelligence();

void AI_Init(Battleship* _this) 
{
	std::cout<<"\nINIT YOUR BOT HERE!!!"<<std::endl;
};

void AI_ChooseFirstPlay(Battleship* _this)
{
	int choose_first_play_arr[3];

	for(int i = 0; i<3;i++)
	{
		choose_first_play_arr[i] = rand()%3;
	}

	_this->SetLottery(choose_first_play_arr);
}

void AI_EndSet(Battleship* _this)
{
	ai->ResetState();
	std::cout<<"*** END SET ***"<<std::endl;
}

void AI_SetBoard(Battleship* _this)
{
	int cSet = _this->GetCurrentSet();
	std::cout<<"Set "<<cSet<<" start!"<<std::endl;

	bool result = false;
	
	switch(cSet)
	{
		case 1:
			// set board for Set 1
			result = _this->SetMyBoard(my_map_1);
			break;
		case 2:
			// set board for Set 2
			result = _this->SetMyBoard(my_map_2);
			break;
		case 3:
			// set board for Set 3
			result = _this->SetMyBoard(my_map_3);
			break;
		case 4:
			// set board for Set 4
			result = _this->SetMyBoard(my_map_4);
			break;
		case 5:
			// set board for Set 5
			result = _this->SetMyBoard(my_map_5);
			break;
		default:
			// Out of set
			break;
	}
	if(result)
	{
		std::cout<<"result: SUCCESS"<<std::endl;
	}
	else
	{
		std::cout<<"result: ERROR -- "<<std::endl;
	}

	ai->PrepareForNewSet();
};

void AI_Update(Battleship* _this)
{
	ai->FindToDestroy(_this);

	// Sent attack position
	ai->Attack(_this);
}

///////////////////////////////////////////////////////
////					DON'T TOUCH MAIN
///////////////////////////////////////////////////////
int main(int argc, char* argv[]) {
	srand ( time(NULL) );
	AppClientHandlerPtr temp(new AppClientHandler());
	s_con_handler = temp;
	
	//AI process
	Get_BOT_Name(argc,argv);

	Battleship::Init = &AI_Init;
	Battleship::ChooseFirstPlay = &AI_ChooseFirstPlay;
	Battleship::SetBoard = &AI_SetBoard;
	Battleship::Update = &AI_Update;
	Battleship::EndSet = &AI_EndSet;

	p_Battleship = new Battleship(bot_table_x, bot_table_y, bot_name, bot_avatar);

	boost::thread thr_cGame(boost::bind(&Battleship::Run,p_Battleship,s_con_handler));
		//Server process
		Connectiont();
		//End server process
	thr_cGame.join();

	delete p_Battleship;
	//End AI process	
    return 0;
}




