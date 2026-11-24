import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ConversationStartersVO, ConversationStartersForm, ConversationStartersQuery } from '@/api/system/conversationStarters/types';

/**
 * 查询对话开场列表
 * @param query
 * @returns {*}
 */

export const listConversationStarters = (query?: ConversationStartersQuery): AxiosPromise<ConversationStartersVO[]> => {
  return request({
    url: '/system/conversationStarters/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询对话开场详细
 * @param id
 */
export const getConversationStarters = (id: string | number): AxiosPromise<ConversationStartersVO> => {
  return request({
    url: '/system/conversationStarters/' + id,
    method: 'get'
  });
};

/**
 * 新增对话开场
 * @param data
 */
export const addConversationStarters = (data: ConversationStartersForm) => {
  return request({
    url: '/system/conversationStarters',
    method: 'post',
    data: data
  });
};

/**
 * 修改对话开场
 * @param data
 */
export const updateConversationStarters = (data: ConversationStartersForm) => {
  return request({
    url: '/system/conversationStarters',
    method: 'put',
    data: data
  });
};

/**
 * 删除对话开场
 * @param id
 */
export const delConversationStarters = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/conversationStarters/' + id,
    method: 'delete'
  });
};
