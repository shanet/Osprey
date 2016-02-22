class OspreyException(Exception):
  pass

class InvalidCommandError(OspreyException):
  pass

class RadioReceiveError(OspreyException):
  pass
