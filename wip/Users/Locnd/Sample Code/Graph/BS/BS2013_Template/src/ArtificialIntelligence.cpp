#include "ArtificialIntelligence.h"

using namespace std;

ArtificialIntelligence::ArtificialIntelligence()
{
	// Direction of moving.
	director = NONE;
	orientation = NONE;

	attackPosX = NONE;
	attackPosY = NONE;

	lastHitX = -1;
	lastHitY = -1;

	planStepLength = 0;

	SetPlan(RANDOM_ATTACK, LARGE_TARGET);
}

ArtificialIntelligence::~ArtificialIntelligence()
{
	delete plan_A;
	delete myBoard;
	delete opponentBoard;
	std::cout<< "Destructured!" << std::endl;
}

void ArtificialIntelligence::PrepareForNewSet()
{
	// Set plan for new set.
	SetPlan(RANDOM_ATTACK, LARGE_TARGET);
	battleshipIsDistroyed = false;
	aircraftCarrierIsDistroyed = false;

	planStepLength = 0;

	for(int i=0; i<100; i++)
	{
		// initialize available_to_shoot array.
		available_to_shoot[i] = i;

		// initialize plans.
		//if (i % 11 == 0 || i % 9 == 0)
		if (i % 2 == 0)
		{
			int adjustment = i / 10 % 2;
			plan_A[planStepLength++] = i + adjustment;
			std::cout<< i << ", ";
		}
	}
	
	std::cout<< std::endl;
}

bool ArtificialIntelligence::Attack(Battleship * battleship)
{
	std::cout<<"\nAttack: director: " << director << ", pos: "<<attackPosX<<";"<<attackPosY<<std::endl;
	
	// Mark attacked positions.
	available_to_shoot[attackPosY * 10 + attackPosX] = -1;

	return battleship->SetAttackPos(attackPosX, attackPosY);
}

void ArtificialIntelligence::ResetState()
{
	attackPosX = 0;
	attackPosY = 0;
	director = NONE;
	lastHitX = -1;
	lastHitY = -1;
	orientation = NONE;

	std::cout<< "Reset State!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" << std::endl;
}

bool ArtificialIntelligence::IsValidPath(int x, int y)
{
	std::cout<< "Check path: " << x << ", " << y << ", Direct: " << director << ", value: " <<  available_to_shoot[y*10+x] << std::endl;
	return available_to_shoot[y*10+x] != -1 && x >= 0 && x < TABLE_SIZE && y >= 0 && y < TABLE_SIZE;
}

int ArtificialIntelligence::GetBaseStepFromPlan(int plan)
{
	switch(plan)
	{		
		case OUTSIDE_ATTACK:
			return 0;
		case INSIDE_ATTACK:
			return targetSize / 2;
		// RANDOM ATTACK:
		default:
			srand(time(NULL));
			return rand() % targetSize;
	}
}


//--------------------------------------------------//
// Set plan to current plan adn update attack data.	//
// newPlan: new value for current plan.				//
//--------------------------------------------------//
void ArtificialIntelligence::SetPlan(int newPlan, int newTarget)
{
	currentPlan = newPlan;
	targetSize = newTarget;
}

//----------------------------------------------------//
// Get attack position that is based on current plan. //
// indexOfData: index of data in array.				  //
// currentPlan: current plan.						  //
//----------------------------------------------------//
int ArtificialIntelligence::GetDataFromPlan(int indexOfData)
{
	switch(targetSize)
	{
		case LARGE_TARGET:
			// Get points that are on crossed line of map with large distance as: 0, 4,8,11,13,...
			// indexOfData - (indexOfData % 5 % 2) + (indexOfData / 10 % 2) always is in of range with indexOfData get value from 0 to 49;
			return plan_A[indexOfData - (indexOfData % 5 % 2) + (indexOfData / 10 % 2)];
		//case SMALL_TARGET:
		default:
			// Get points that are on crossed line of map with small distance as: 0, 2, 4,6, 8,...
			return plan_A[indexOfData];
	}
}

bool ArtificialIntelligence::UpdateAttackPoint()
{
	std::cout<< "Update Attack Point: orientation = " << (orientation == NONE ? "NONE" : (orientation == HORIZONTAL ? "HORIZONTAL" : "VERTICAL")) << std::endl;
	switch(director)
	{
		case TOP:
			if (!IsValidPath(attackPosX, attackPosY - 1))
			{
				if (orientation != NONE)
				{
					director -= 2;
					attackPosY = lastHitY;
				}
				else
				{
					director--;
				}
				return false;
			}

			attackPosY--;
			break;
		case LEFT:
			if (!IsValidPath(attackPosX - 1, attackPosY))
			{
				director--;
				return false;
			}

			attackPosX--;
			break;
		case BOTTOM:
			if (!IsValidPath(attackPosX, attackPosY + 1))
			{
				if (orientation != NONE)
				{
					director -= 2;
					attackPosY = lastHitY;
				}
				else
				{
					director--;
				}
				return false;
			}

			attackPosY++;
			break;
		case RIGHT:
			if (!IsValidPath(attackPosX + 1, attackPosY))
			{
				if (orientation != NONE)
				{
					director -= 2;
					attackPosX = lastHitX;
				}
				else
				{
					director--;
				}

				return false;
			}

			attackPosX++;
			break;
		case NONE:
			// Random attack to find ship.
			std::cout<< "Plan Step Length: " << planStepLength << std::endl;

			// TODO: Hardcode here. Need to replace param later.
			int baseStep = GetBaseStepFromPlan(currentPlan);
			if (planStepLength != 0)
			{
				if (planStepLength == LARGE_TARGET)
				{
					targetSize = SMALL_TARGET;
				}

				for(int j = 0; j < 50; j++)
				{
					if (baseStep - j >= 0 && available_to_shoot[GetDataFromPlan(baseStep - j)] >= 0)
					{
						attackPosX = GetDataFromPlan(baseStep - j) % 10;
						attackPosY = GetDataFromPlan(baseStep - j) / 10;
						std::cout<< "Plan A: " << attackPosX << ", " << attackPosY << std::endl;
						planStepLength--;
						return true;
					}

					if (baseStep + j >= 50 || available_to_shoot[GetDataFromPlan(baseStep + j)] < 0)
					{
						continue;
					}

					attackPosX = GetDataFromPlan(baseStep + j) % 10;
					attackPosY = GetDataFromPlan(baseStep + j) / 10;
					std::cout<< "Plan A: " << attackPosX << ", " << attackPosY << std::endl;
					planStepLength--;
					return true;
				}

				if (targetSize == LARGE_TARGET)
				{
					targetSize = SMALL_TARGET;
				}
				else
				{
					planStepLength = 0;
				}
			}
			
			// Use this plan when A plan cannot be used anymore.
			std::cout<< "-------------Change to plan B----------" << std::endl;
			for (int nextPosition = 0; nextPosition < 100; nextPosition++)
			{
				if (available_to_shoot[nextPosition] < 0)
				{
					continue;
				}

				attackPosX = nextPosition % 10;
				attackPosY = nextPosition / 10;

				return true;
			}
			
			break;
	}

	return true;
}

//------------------------------------------//
// This is invoked when opponent is shooted.//
//==========================================//
void ArtificialIntelligence::FindToDestroy(Battleship * ship)
{
	// Get the board status at position X,Y 
	// Note: it will return status of previous turn
	if(attackPosX < 0 || attackPosY < 0)
	{
		return;
	}
		
	// Get my current board  status
	ship->GetMyBoard(myBoard);

	// Get opponent current board  status, it use to calculate the next attack position
	ship->GetOpponentBoard(opponentBoard);

	
	int lastResult = ship->GetBoardStatusAt(opponentBoard, attackPosX, attackPosY);
		
	switch(lastResult)
	{
		case HIT:
			std::cout<<"Attack HIT at "<<attackPosX<<";"<<attackPosY<<std::endl;
			if (lastHitX == -1 || lastHitY == -1)
			{
				// First hit.
				lastHitX = attackPosX;
				lastHitY = attackPosY;
				director = BOTTOM;
			}
			else
			{
				switch(director)
				{
					case BOTTOM:
					case TOP:
						orientation= VERTICAL;
						break;
					case LEFT:
					case RIGHT:
						orientation= HORIZONTAL;
						break;
				}

				std::cout<< "Orientation is found: " << (orientation == NONE ? "NONE" : 
					(orientation == HORIZONTAL ? "HORIZONTAL" : "VERTICAL")) << std::endl;
			}
			break;
		case MISS:
			std::cout<<"Attack MISS at "<<attackPosX<<";"<<attackPosY<<std::endl;
			if (lastHitX != -1 && lastHitY != -1)
			{
				if (orientation == NOT_FOUND){
					// change director BOTTOM -> RIGHT -> TOP -> LEFT.
					director--;
				}
				else
				{
					switch(director)
					{
						case BOTTOM:
						case RIGHT:
							director = director - 2;
							break;
						case TOP:
						case LEFT:
							director = director + 2;
							break;
					}
				}

				attackPosX = lastHitX;
				attackPosY = lastHitY;
			}
			break;
		case EMPTY:
			std::cout<<"EMPTY at "<<attackPosX<<";"<<attackPosY<<std::endl;
			break;
		default:
			if (lastResult == BATTLESHIP)
			{
				battleshipIsDistroyed = true;
			}

			if (lastResult == AIRCRAFT_CARRIER)
			{
				aircraftCarrierIsDistroyed = true;
			}

			if (battleshipIsDistroyed && aircraftCarrierIsDistroyed)
			{
				targetSize = SMALL_TARGET;
			}

			std::cout<<"Ship "<<lastResult<<" destroyed!"<<std::endl << "Ignored points: ";
			// Reduce the points that can be used to attack.
			int unitX = 0, unitY = 0, row = 0, col = 0;
			if (orientation == VERTICAL)
			{
				row = 1;
				unitY = director == TOP ? 1 : -1;
			}
			else 
			{
				unitX = director == LEFT ? 1 : -1;
				col = 1;
			}

			// Addition adjustment.
			int adjustmentX = (director == LEFT ? col * lastResult : col);
			int adjustmentY = (director == TOP ? row * lastResult : row);
			if ((orientation == VERTICAL && attackPosY + adjustmentY < TABLE_SIZE) || 
				(orientation != VERTICAL && attackPosX + adjustmentX < TABLE_SIZE))
			{
				available_to_shoot[(attackPosY + adjustmentY) * 10 
					+ attackPosX + adjustmentX] = -1;
				std::cout << (attackPosY + adjustmentY) * 10 
					+ attackPosX + adjustmentX << "*, ";
			}

			// Subtraction adjustment.
			adjustmentX = (director == LEFT ? -col : -col * lastResult );
			adjustmentY = (director == TOP ? -row : -row * lastResult );
			if ((orientation == VERTICAL && attackPosY + adjustmentY >= 0) || 
				(orientation != VERTICAL && attackPosX + adjustmentX >= 0))
			{
				available_to_shoot[
					(attackPosY + adjustmentY) * 10 
						+ attackPosX + adjustmentX] = -1;
				std::cout << (attackPosY + adjustmentY) * 10 
						+ attackPosX + adjustmentX << "*, ";
			}

			bool checkBottomRight = (orientation == VERTICAL && attackPosX + 1 < TABLE_SIZE)
				|| (orientation != VERTICAL && attackPosY + 1 < TABLE_SIZE);
			bool checkTopLeft =(orientation == VERTICAL && attackPosX - 1 >= 0) || 
				(orientation != VERTICAL && attackPosY - 1 >= 0);

			for (int i = 0; i < lastResult; i++)
			{
				if (checkBottomRight)
				{
					available_to_shoot[(attackPosY + unitY * i + col ) * 10 + attackPosX + unitX * i + row] = -1;
					std::cout << (attackPosY + unitY * i + col ) * 10 + attackPosX + unitX * i + row << ", ";
				}
				if (checkTopLeft)
				{
					available_to_shoot[(attackPosY + unitY * i - col) * 10 + attackPosX + unitX * i - row] = -1;
					std::cout << (attackPosY + unitY * i - col) * 10 + attackPosX + unitX * i - row << ", ";
				}
			}	
			std::cout << std::endl;

			// Change targetSize if need.
			ResetState();
			break;
	}

	// Get attack position.
	while(!UpdateAttackPoint())
	{
		std::cout<< "Update attack point unsuccessfully!" << std::endl;
		if (director < 0){director = 0;}
	}
}