#ifndef _CONSTANT_
#define _CONSTANT_

////////////////////////////////
////	BOARD STATUS		////
////////////////////////////////

const int MISS								= -1;
const int EMPTY								= 0;
const int HIT								= 1;
const int DISTROYER							= 2;
const int PATROL							= 3;
const int SUBMARINE							= 3;
const int BATTLESHIP						= 4;
const int AIRCRAFT_CARRIER					= 5;

const int TIME_SETBOAR						= 10;
const int TIME_TURN							= 2;

const int NONE								= 0;
const int LEFT								= 1;
const int TOP								= 2;
const int RIGHT								= 3;
const int BOTTOM							= 4;

const int NOT_FOUND							= 0;
const int HORIZONTAL						= 1;
const int VERTICAL							= 2;
const int TABLE_SIZE						= 10;
const int MAX_PLAN_STEP						= 50;

const int RANDOM_ATTACK						= 0;
const int OUTSIDE_ATTACK					= 1;
const int INSIDE_ATTACK						= 2;
#endif