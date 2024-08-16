package model.game

/** Represents the effect of a card. */
trait CardEffect

/** Represents the effect of a cactus card. */
enum CactusCardEffect extends CardEffect:
  case AceEffect, JackEffect, NoEffect
