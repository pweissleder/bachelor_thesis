FROM python:3.10
# Oriented on equivalent file from the Unicorn Project
# https://www.docker.com/blog/how-to-dockerize-your-python-applications/

WORKDIR /gateway

COPY . /gateway

RUN pip install --upgrade pip
RUN pip3 install --no-cache-dir -r configuration/requirements.txt

ENV PYTHONPATH "${PYTHONPATH}:/gateway"

CMD ["python3", "src/app.py"]