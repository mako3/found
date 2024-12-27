package mako3.found.json;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Component
public class JsonParser {

    public List<MessageJson> parseMessages(String spaceId, File file) throws JsonException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(file);
            ArrayNode arrayNode = (ArrayNode) node.get("messages");

            List<MessageJson> list = new ArrayList<>();
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode n = arrayNode.get(i);
                MessageJson message = mapper.treeToValue(n, MessageJson.class);
                if (!message.getMessageId().startsWith(spaceId)) {
                    throw new JsonException("jsonファイル中に記載されたspaceIdが、変更対象のspaceIdと一致しません");
                }
                list.add(message);
            }

            return list;
        } catch (JsonException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonException("messages.jsonのパース時にエラーが発生しました", e);
        }
    }

    public List<GroupMemberJson> parseMembers(String spaceName, File file) throws JsonException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(file);
            String jsonSpaceName = node.get("name").asText();
            if (!spaceName.equals(jsonSpaceName)) {
                throw new JsonException("jsonファイル中に記載されたspaceNameが、変更対象のspaceNameと一致しません");
            }
            ArrayNode arrayNode = (ArrayNode) node.get("members");
            List<GroupMemberJson> list = new ArrayList<>();
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode n = arrayNode.get(i);
                GroupMemberJson groupMember = mapper.treeToValue(n, GroupMemberJson.class);
                list.add(groupMember);
            }
            return list;
        } catch (JsonException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonException("group_info.jsonのパース時にエラーが発生しました", e);
        }
    }

}
