#include <ctime>
#include <stdio.h>
#include <stdlib.h>
#include "../include/battleship/Battleship.h"
//#include "constant.h"

class ArtificialIntelligence
{
private:
	// Direction of moving.
	int director;
	
	// Identifier: 0: have not found, 1: horizontal, 2: vertical
	int orientation;

	// Current attack position
	int attackPosX;
	int attackPosY;

	// Last hit attack position
	int lastHitX;
	int lastHitY;
	
	int planStepLength;

	// List position to shoot. 1: can shoot. 0: cannot shoot.
	int available_to_shoot[100];
	int plan_A[50];
	int myBoard[100];
	int opponentBoard[100];

public:
	ArtificialIntelligence();
	~ArtificialIntelligence();

	//------------------//
	// Prepare new set  //
	//------------------//
	void PrepareForNewSet();
	
	//---------------------------------------------------------//
	// Reset all necessary fields to prepare for new attacking //
	//---------------------------------------------------------//
	void ResetState();
	
	//-----------------------------------------------------------//
	// Check the specified position is available to shoot or not //
	// x: x coordination of checked position.					 //
	// y: y coordination of checked position.					 //
	//-----------------------------------------------------------//
	bool IsValidPath(int x, int y);

	//------------------------------------------//
	int GetRandomNumber(int a, int b, int m);

	//--------------------------------------------------------------//
	// Update attack points to get correct position before attacking//
	//--------------------------------------------------------------//
	bool UpdateAttackPoint();

	//------------------------------------------------------//
	// Send attacking to opponent							//
	// battleship: The intermediate ship to get information //
	//------------------------------------------------------//
	bool Attack(Battleship * battleship);

	//------------------------------------------------------//
	// This is invoked when opponent is shooted.			//
	// battleship: The intermediate ship to get information //
	//======================================================//
	void FindToDestroy(Battleship * ship);
};