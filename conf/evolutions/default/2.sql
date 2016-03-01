# --- !Ups
INSERT INTO media (id, name, media_url) VALUES(1, 'Dialogue 1', 'https://www.dropbox.com/s/iw9pbrysoe56hf0/Grammaire_en_dialogues_No18.mp3?raw=1');
INSERT INTO subtitle (id, pos, "offset", media_id, "text") VALUES
  (1, 1, 11, 1, 'Simon : Est-ce que tu aimes lire ?'),
  (2, 1, 13, 1, 'Clément : Ah oui ; j''adore lire ! En fait, je lis quelque chose chaque soir avant de me coucher'),
  (3, 1, 17.5, 1, 'Simon : Qu''est-ce que tu préfères lire ?'),
  (4, 1, 19, 1, 'Clément : Ça dépend. Certaines jours, je préfère lire un roman , d''autres jours de la poésie...'),
  (5, 1, 24, 1, 'Simon : Qu''est-ce tu lis, en ce moment? Tu lis quelque chose d''intéressant?'),
  (6, 1, 28, 1, 'Clément : Oui, je lis quelque chose de très beau. Je lis quelques poèmes de Prévert.'),
  (7, 1, 33.5, 1, 'Simon : C''est qui, Prévert?'),
  (8, 1, 36.4, 1, 'Clément : Tu ne connais pas Prévert? Mais tu ne connais rien! Tout le monde connaît Prévert. C''est quelqu''un de merveilleux. C''est un grand poète du xx siècle'),
  (9, 1, 42.8, 1, 'Simon : Je connais juste quelques écrivains modernes... Tu sais, moi, la littérature... Tu connais tous les poèmes de Prévert?'),
  (10, 1, 50.5, 1, 'Clément : Oh, oui, je connais plusieurs poésies. Par exemple, j''aime beaucoup \"Pour faire le portrait d''un oiseau\". Je me rappelle seulement quelques phrases par coeur.'),
  (11, 1, 60, 1, 'C''est le début, je crois: \"Peindre d''abord une cage avec une porte ouverte. Peindre ensuite quelque chose de joli, quelque chose de simple, quelque chose de beau, quelque chose d''utile pour l''oiseau..\".'),
  (12, 1, 74.5, 1, 'Simon : Ah oui, c''est pas mal... C''est dans quel livre?'),
  (13, 1, 78, 1, 'Clément : C''est quelque part dans ce livre, Paroles. Ah, voilà, page 154.'),
  (14, 1, 84, 1, 'Tu vois, chaque poème a un style différent: certains poèmes sont très courts, d''autres très longs'),
  (15, 1, 89.5, 1, 'Certains sont romantiques, d''autre ironiques...'),
  (16, 1, 92.8, 1, 'Simon : Et Prévert a écrit quelque chose d''amusant?'),
  (17, 1, 94.7, 1, 'Clément : Oui, bien sûr, il y a plusieurs poèmes amusants: \"Cotège\", ou \"Les Belles Familles\"'),
  (18, 1, 100.5, 1, 'Simon : Tu as vraiment tous les livres de Prévert dans ta bibliothèque?'),
  (19, 1, 103.5, 1, 'Clément : Oui, je crois. J''ai aussi plusieurs livres de poésie américaine, anglaise, allemande...'),
  (20, 1, 109.5, 1, 'Clément : Dis-moi, où est-ce que je peux acheter des livres de Prévert?'),
  (21, 1, 113, 1, 'Simon : Dans toutes les bonnes librairies.');


# --- !Downs
DELETE FROM media;
DELETE FROM subtitle;