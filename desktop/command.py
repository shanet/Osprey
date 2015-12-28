class Command(object):
  COMMAND_ZERO = {'name': 'zero', 'value': 0, 'arg': False}
  COMMAND_SET_PRESSURE = {'name': 'pressure', 'value': 1, 'arg': True}

  COMMANDS = [COMMAND_ZERO, COMMAND_SET_PRESSURE]

  COMMAND_ERR = 0
  COMMAND_ACK = 1

  def __init__(self, input=None):
    self.command = None
    self.arg = None

    if input:
      self.parse(input)

  def encode(self):
    arg = self.arg if self.arg else ''
    return ("%d%s\n" % (self.command['value'], arg)).encode()

  def parse(self, input):
    parts = input.split(' ')
    self.__isValid(parts)

    self.__setCommand(parts)
    self.__setArg(parts)

  def __isValid(self, parts):
    if len(parts) > 2:
      raise InvalidCommandError('Too many arguments: %d' % len(parts))

  def __setCommand(self, parts):
    for command in Command.COMMANDS:
      if command['name'] == parts[0]:
        self.command = command
        return

    raise InvalidCommandError('Unknown command')

  def __setArg(self, parts):
    if len(parts) > 1:
      self.arg = parts[1]
    elif self.command['arg']:
      raise InvalidCommandError('Command \'%s\' requires an argument' % (self.command['name']))

class InvalidCommandError(Exception):
  pass
