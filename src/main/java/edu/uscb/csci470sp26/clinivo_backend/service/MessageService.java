package edu.uscb.csci470sp26.clinivo_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import edu.uscb.csci470sp26.clinivo_backend.model.Conversation;
import edu.uscb.csci470sp26.clinivo_backend.model.ConversationParticipant;
import edu.uscb.csci470sp26.clinivo_backend.model.Message;
import edu.uscb.csci470sp26.clinivo_backend.model.MessageRead;
import edu.uscb.csci470sp26.clinivo_backend.model.User;
import edu.uscb.csci470sp26.clinivo_backend.repository.ConversationRepository;
import edu.uscb.csci470sp26.clinivo_backend.repository.MessageReadRepository;
import edu.uscb.csci470sp26.clinivo_backend.repository.MessageRepository;
import edu.uscb.csci470sp26.clinivo_backend.repository.UserRepository;
import edu.uscb.csci470sp26.clinivo_backend.repository.ConversationParticipantRepository;

@Service
public class MessageService {

	private final MessageRepository messageRepository;
	private final ConversationRepository conversationRepository;
	private final UserRepository userRepository;
	private final ConversationParticipantRepository participantRepository;
	private final MessageReadRepository messageReadRepository;

	public MessageService(MessageRepository messageRepository, ConversationRepository conversationRepository,
			UserRepository userRepository, ConversationParticipantRepository participantRepository,
			MessageReadRepository messageReadRepository) {
		this.messageRepository = messageRepository;
		this.conversationRepository = conversationRepository;
		this.userRepository = userRepository;
		this.participantRepository = participantRepository;
		this.messageReadRepository = messageReadRepository;
	}

	public Message sendMessage(Long conversationId, Long senderId, String content) {

		Conversation conversation = conversationRepository.findById(conversationId)
				.orElseThrow(() -> new RuntimeException("Conversation not found"));

		User sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("User not found"));

		// FIXED: Compare IDs, not object references
		if (!sender.getId().equals(conversation.getDoctor().getId())
				&& !sender.getId().equals(conversation.getPatient().getId())) {
			throw new RuntimeException("Sender is not part of this conversation");
		}

		Message message = new Message();
		message.setConversation(conversation);
		message.setSender(sender);
		message.setContent(content);

		message = messageRepository.save(message);

		List<ConversationParticipant> participants = participantRepository.findByConversationId(conversationId);

		for (ConversationParticipant p : participants) {
			if (!p.getUser().getId().equals(senderId)) {

				MessageRead read = new MessageRead();
				read.setMessage(message);
				read.setUser(p.getUser());
				read.setRead(false);

				messageReadRepository.save(read);
			}
		}

		return message;
	}

	public List<Message> getMessages(Long conversationId) {

		Conversation conversation = conversationRepository.findById(conversationId)
				.orElseThrow(() -> new RuntimeException("Conversation not found"));

		return messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
	}

	public void markMessagesAsRead(Long conversationId, Long userId) {

		Conversation conversation = conversationRepository.findById(conversationId)
				.orElseThrow(() -> new RuntimeException("Conversation not found"));

		List<Message> messages = messageRepository.findByConversationOrderByCreatedAtAsc(conversation);

		for (Message message : messages) {

			MessageRead read = messageReadRepository.findByMessageIdAndUserId(message.getId(), userId).orElse(null);

			if (read != null && !read.isRead()) {
				read.setRead(true);
				read.setReadAt(LocalDateTime.now());
				messageReadRepository.save(read);
			}
		}
	}

	public int getUnreadCount(Long userId) {
		return messageReadRepository.findByUserIdAndIsReadFalse(userId).size();
	}
}
