package model;

import chess.ChessGame;

public record GameData (int gameIDe, String whiteUsername, String blackUsername, String gameName, ChessGame game){}
