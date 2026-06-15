SET search_path TO "wedding-invitation";

CREATE TABLE IF NOT EXISTS admin
(
    id         BIGSERIAL    NOT NULL,
    username   VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT pk_admin PRIMARY KEY (id),
    CONSTRAINT uq_admin_username UNIQUE (username)
    );

CREATE TABLE IF NOT EXISTS guest
(
    id         BIGSERIAL   NOT NULL,
    name       VARCHAR(50) NOT NULL,
    side       VARCHAR(10) NOT NULL,
    token      VARCHAR(36) NOT NULL,
    is_active  BOOLEAN     NOT NULL DEFAULT true,
    created_at TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT pk_guest PRIMARY KEY (id),
    CONSTRAINT uq_guest_token UNIQUE (token),
    CONSTRAINT chk_guest_side CHECK (side IN ('GROOM', 'BRIDE'))
    );

CREATE TABLE IF NOT EXISTS rsvp
(
    id         BIGSERIAL   NOT NULL,
    guest_id   BIGINT      NOT NULL,
    status     VARCHAR(10) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT pk_rsvp PRIMARY KEY (id),
    CONSTRAINT uq_rsvp_guest UNIQUE (guest_id),
    CONSTRAINT fk_rsvp_guest FOREIGN KEY (guest_id) REFERENCES guest (id),
    CONSTRAINT chk_rsvp_status CHECK (status IN ('ATTENDING', 'ABSENT'))
    );

CREATE TABLE IF NOT EXISTS message
(
    id         BIGSERIAL NOT NULL,
    guest_id   BIGINT    NOT NULL,
    content    TEXT      NOT NULL,
    is_visible BOOLEAN   NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT pk_message PRIMARY KEY (id),
    CONSTRAINT fk_message_guest FOREIGN KEY (guest_id) REFERENCES guest (id)
    );

CREATE TABLE IF NOT EXISTS invitation_view
(
    id         BIGSERIAL   NOT NULL,
    guest_id   BIGINT      NOT NULL,
    viewed_at  TIMESTAMP   NOT NULL DEFAULT now(),
    ip_address VARCHAR(45),
    CONSTRAINT pk_invitation_view PRIMARY KEY (id),
    CONSTRAINT fk_invitation_view_guest FOREIGN KEY (guest_id) REFERENCES guest (id)
    );