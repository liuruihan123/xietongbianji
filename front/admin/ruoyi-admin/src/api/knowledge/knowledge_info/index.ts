import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import {
  Knowledge_infoVO,
  Knowledge_infoForm,
  Knowledge_infoQuery,
  KnowledgeDelReq
} from '@/api/knowledge/knowledge_info/types';
import { TypeVO, TypeForm, TypeQuery } from '@/api/system/type/types';
/**
 * 查询知识库管理列表
 * @param query
 * @returns {*}
 */

export const listKnowledge_info = (query?: Knowledge_infoQuery): AxiosPromise<Knowledge_infoVO[]> => {
  return request({
    url: '/knowledge/all',
    method: 'get',
    params: query
  });
};


export const listKnowledge_type  = (query?: TypeQuery): AxiosPromise<TypeVO[]> => {
  return request({
    url: '/system/type/list',
    method: 'get',
    params: query
  });
};


/**
 * 查询知识库管理详细
 * @param id
 */
export const getKnowledge_info = (id: string | number): AxiosPromise<Knowledge_infoVO> => {
  return request({
    url: '/knowledge/' + id,
    method: 'get'
  });
};

/**
 * 新增知识库管理
 * @param data
 */
export const addKnowledge_info = (data: Knowledge_infoForm) => {
  return request({
    url: '/knowledge/save',
    method: 'post',
    data: data
  });
};

/**
 * 修改知识库管理
 * @param data
 */
export const updateKnowledge_info = (data: Knowledge_infoForm) => {
  return request({
    url: '/knowledge/update',
    method: 'put',
    data: data
  });
};

/**
 * 删除知识库管理
 * @param params
 */
// export const delKnowledge_info = (id: string | number | Array<string | number>) => {
export const delKnowledge_info = (params:KnowledgeDelReq) => {

  return request({
    url: '/knowledge/remove' ,
    method: 'post',
    data: params
  });
};
