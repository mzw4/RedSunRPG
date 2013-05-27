package redsun.events;

public enum EventType {
  Event_Game_ActorCreated,
  Event_Game_ActorDestroyed,
  Event_Game_ActorMoved,
  Event_Game_ActorStopped,
  Event_Game_ActorSpeedSlow,
  Event_Game_ActorSpeedFast,
  Event_Game_EnterCombat,
  Event_Game_ExitCombat,
  Event_Game_EnterInGame,
  Event_Game_EnterTitle,
  Event_Game_StartNewGame,

  Event_UI_CameraPanStarted,
  Event_UI_CameraPanEnded,
  Event_UI_CursorSelect,
  Event_UI_CursorDeSelect,
  Event_UI_CursorSpeedFast,
  Event_UI_CursorSpeedSlow,
  Event_UI_DialogueOpened,
  Event_UI_DialogueCont,
  Event_UI_DialogueClosed,
  Event_UI_MenuOpened,
  Event_UI_MenuClosed,
  Event_UI_FadeTransition,
  Event_UI_TransitionStarted,
  Event_UI_TransitionEnded,
  
  Event_System_GameClosed
}
