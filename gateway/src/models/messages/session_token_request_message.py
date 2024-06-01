from dataclasses import dataclass

from dataclasses_json import LetterCase, dataclass_json


@dataclass_json(letter_case=LetterCase.CAMEL)
@dataclass
class GatewaySessionTokenRequestMessage:
    id: str
    auth_token: str

    def to_dict(self):
        return {"id": self.id, "authToken": self.auth_token}
