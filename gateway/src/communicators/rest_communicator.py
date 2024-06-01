import asyncio
import logging

import aiohttp
import json

from yarl import URL

from src.models.messages.session_token_request_message import GatewaySessionTokenRequestMessage

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)


class RestCommunicator:
    def __init__(self, base_url, port, authentication_endpoint):
        self.base_url = base_url
        self.port = port
        self.authentication_endpoint = authentication_endpoint
        self.session = None

    async def __aenter__(self):
        if self.session is None:
            self.session = aiohttp.ClientSession()
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        if self.session:
            await self.session.close()
            self.session = None

    async def get_session_token(self, gateway_id, auth_token):
        while True:
            try:
                url = URL(f"{self.base_url}:{self.port}{self.authentication_endpoint}")
                session_token_request_message = GatewaySessionTokenRequestMessage(id=gateway_id, auth_token=auth_token)

                async with self.session.post(url, data=json.dumps(session_token_request_message.to_dict()),
                                             headers={'Content-Type': 'application/json'}) as resp:
                    response = await resp.json()
                    gateway_id = response['gatewayId']
                    session_token = response['sessionToken']
                    expire_date = response['expireDate']

                return gateway_id, session_token, expire_date
            except OSError:
                raise

    def is_connected(self) -> bool:
        if self.session and not self.session.closed:
            return True
        return False

    async def close_session(self):
        # Provided for explicit closure outside of context management
        if self.session:
            await self.session.close()
            self.session = None
