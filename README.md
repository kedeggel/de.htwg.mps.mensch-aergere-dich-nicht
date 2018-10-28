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

## TUI

> tui modes expects a monospace font.

`s` - start field
`o` - normal field

`a` `b` `c` `d` normal or start field occupied by peg


`h` - empty home field
`x` - occupied home field


Example for board where all pegs are out:
```
a:4     o o s     b:4
        o h o
        o h o
        o h o
s o o o o h o o o o o
o h h h h   h h h h o
o o o o o h o o o o s
        o h o
        o h o
        o h o
d:4     s o o     c:4
```

Example for board with 2 pegs of `a`, 1 beg of `b` on the field and one occupied home field of `c`:
```
a:2     o o s     b:3
        o h o
        o h o
        o h o
a o o o o h o o o o o
o h h h h   h x h h o
o o o o o h o o o o s
        a h o
        o h o
        o h o
d:4     b o o     c:3
```