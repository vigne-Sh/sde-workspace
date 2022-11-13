import logging


def create_logger(module_name) -> object():

    formatting_style = "[%(asctime)s] %(levelname)s [%(name)s:%(lineno)s] %(message)s"
    formatter = logging.Formatter(fmt=formatting_style)

    handler = logging.StreamHandler()
    handler.setFormatter(formatter)

    logger = logging.getLogger(module_name)
    logger.setLevel(logging.DEBUG)
    logger.addHandler(handler)
    return logger
