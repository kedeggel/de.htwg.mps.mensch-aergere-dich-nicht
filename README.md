# Mensch ärgere dich nicht!
Game written in Scala for lecture 'Modern Programming Languages'

## Rules

Number of players: 2-4

The game starts with 4 pegs in the *out* area at the beginning of the game.

Goal is it to move all pegs to the *home* row.

 + When rolling a 6 the player has to move a peg out of the *out* area into the
   start area and can roll the dice again.

 + The start area has to be freed as fast as possible.

 + When the players pegs comes onto a field with enemy peg, the enmey peg gets
   moved to his *out* area.

 + Is the *home* field already occupied the peg can't be moved and it's the
   next players turn.

### Optional Rules

 + If a player has no pegs on the field he can throw the dice 3 times.

 + If a can kick out a enemy peg, he has to do it. (high priority then freeing
   the *start* field)

 + It's not allowed to jump over pegs in the *home* row
